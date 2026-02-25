package com.alirizakaygusuz.gymcrm.service.auth;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginResponse;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.monitoring.metrics.AppMetrics;
import com.alirizakaygusuz.gymcrm.security.authentication.jwt.JwtService;
import com.alirizakaygusuz.gymcrm.security.ratelimit.LoginRateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppMetrics appMetrics;
    private final LoginRateLimitService loginRateLimitService;



    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Authenticating user with username: {}", request);

        appMetrics.incrementLoginAttempts();



        User user = findUserByUsernameWithDetailsOrThrow(request.username());

        if (!checkPassword(request.password(), user.getPassword())) {
            log.warn("Authentication failed for user with username: {}", request.username());

            appMetrics.incrementLoginFailure();

            loginRateLimitService.recordFailedAttempt(request.username());

            throw new AuthenticationFailedException("Invalid username or password");
        }
        log.info("User with username: {} authenticated successfully", request.username());

        appMetrics.incrementLoginSuccess();

        loginRateLimitService.resetAttempts(request.username());


        String accessToken = jwtService.generateToken(user.getUsername(),user.getAuthorities());
        long expirationTime = jwtService.getExpirationTime();


        return new LoginResponse(accessToken,"Bearer", expirationTime);

    }

    private User findUserByUsernameWithDetailsOrThrow(String username) {
        return userDao.findByUsernameWithDetails(username).orElseThrow(() -> {
            log.warn("User with username {} not found", username);
            return new AuthenticationFailedException("Invalid username or password");
        });
    }


    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        User user = findUserByUsernameOrThrow(request.username());

        log.info("Changing password for user with username={}", user.getUsername());
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userDao.update(user);

    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        User user = userDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Authentication failed: user with username {} not found", username);
            return new AuthenticationFailedException("Invalid username or password");
        });

        boolean isAuthenticated = checkPassword(password, user.getPassword());
        if (isAuthenticated) {
            log.info("User with username {} authenticated successfully", username);
        } else {
            log.warn("Authentication failed for user with username {}", username);
        }
        return isAuthenticated;
    }




    private User findUserByUsernameOrThrow(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> {
            log.warn("User with username {} not found", username);
            return new AuthenticationFailedException("Invalid username or password");
        });
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        log.info("Checking password validity");
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


}
