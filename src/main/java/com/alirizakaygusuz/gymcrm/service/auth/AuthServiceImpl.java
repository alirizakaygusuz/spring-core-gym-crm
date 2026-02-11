package com.alirizakaygusuz.gymcrm.service.auth;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final CommonValidator commonValidator;

    @Transactional(readOnly = true)
    public User authenticateAndGetUser(LoginRequest request) {
        log.info("Validating authentication parameters");
        commonValidator.validateNotNull(request, "Login request");
        commonValidator.validateNotBlank(request.username(), "Username");
        commonValidator.validateNotBlank(request.password(), "Password");

        log.info("Authenticating user with username: {}", request.username());
        User user = findUserByUsernameOrThrow(request.username());

        if (!checkPassword(request.password(), user.getPassword())) {
            log.warn("Authentication failed for user with username: {}", request.username());
            throw new AuthenticationFailedException("Invalid username or password");
        }
        log.info("User with username: {} authenticated successfully", request.username());

        return user;
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
