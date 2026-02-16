package com.alirizakaygusuz.gymcrm.service.user;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.common.UserUpdateRequest;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDomainService {

    private final PasswordEncoder passwordEncoder;
    private final CredentialsGenerator credentialsGenerator;
    private final UserValidator userValidator;
    private final CommonValidator commonValidator;

    public User createWithCredentials(UserRegisterRequest request) {
        log.info("Creating user profile with provided data: firstName={}, lastName={}",
                request.firstName(), request.lastName());

        String username = credentialsGenerator.generateUniqueUsername(
                request.firstName(), request.lastName()
        );
        String rawPassword = credentialsGenerator.generateRandomPassword();

        log.info("Generating credentials for username={}", username);


        return buildUser(request, username, rawPassword);
    }

    private User buildUser(UserRegisterRequest request, String username, String rawPassword) {
        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setActive(true);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        return user;
    }


    public void applyUpdate(User user, UserUpdateRequest request) {

        commonValidator.validateNotNull(user, "User");
        log.info("Applying updates to user profile with username={}", user.getUsername());

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setActive(request.isActive());
    }


    public void changePassword(User user, String newPassword) {
        log.info("Changing password for user with username={}", user.getUsername());
        user.setPassword(passwordEncoder.encode(newPassword));
    }


    public void setActiveStatus(User user, boolean desiredActive) {
        String username = user.getUsername();
        log.info("{} user with username={}", desiredActive ? "Activating" : "Deactivating", username);

        if (user.isActive() == desiredActive) {
            String msg = desiredActive ? "User is already active" : "User is already inactive";
            log.warn("{} for username={}", msg, username);
            throw new ValidationException(msg);
        }

        user.setActive(desiredActive);
    }

}
