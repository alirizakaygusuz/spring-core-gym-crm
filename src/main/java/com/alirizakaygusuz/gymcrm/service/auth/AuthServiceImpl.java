package com.alirizakaygusuz.gymcrm.service.auth;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.validator.SelfAccessValidator;
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

    private final SelfAccessValidator selfAccessValidator;


    @Override
    @Transactional(readOnly = true)
    public void login(String username, String password) {
        log.info("Authenticating user with username: {}", username);
        User user = findUserByUsernameOrThrow(username);

        if (!checkPassword(password, user.getPassword())) {
            log.warn("Authentication failed for user with username: {}", username);
            throw new AuthenticationFailedException("Invalid username or password");
        }
        log.info("User with username: {} authenticated successfully", username);

    }

    @Override
    @Transactional
    public void changePassword(String currentUsername, ChangePasswordRequest request) {

        selfAccessValidator.assertSelfAccess(currentUsername, request.username());

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
