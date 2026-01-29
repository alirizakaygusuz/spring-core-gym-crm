package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dto.common.UserProfileData;
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

    public User createWithCredentials(UserProfileData request) {
        log.info("Creating user profile and checking request validity");

        validateUserProfileData(request);


        String username = credentialsGenerator.generateUniqueUsername(
                request.firstName(), request.lastName()
        );
        String rawPassword = credentialsGenerator.generateRandomPassword();

        log.info("Generating credentials for username={}", username);


        return buildUser(request, username, rawPassword);
    }

    public void applyUpdate(User user, UserProfileData request) {

        commonValidator.validateNotNull(user, "User");
        log.info("Applying updates to user profile with username={}", user.getUsername());

        validateUserProfileData(request);

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setActive(request.isActive());
    }


    public void changePassword(User user, String newPassword) {
        commonValidator.validateNotNull(user, "User");
        commonValidator.validateNotBlank(newPassword, "New password");
        log.info("Changing password for user with username={}", user.getUsername());
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public void activate(User user) {
        changeActiveStatus(user, true);
    }

    public void deactivate(User user) {
        changeActiveStatus(user, false);
    }

    private void validateUserProfileData(UserProfileData request) {
        commonValidator.validateNotNull(request, "User profile creation request");
        userValidator.validateRequiredUserNames(request.firstName(), request.lastName());
        commonValidator.validateNotNull(request.isActive(), "Active status");
    }


    private void changeActiveStatus(User user, boolean desiredActive) {
        commonValidator.validateNotNull(user, "User");
        String username = user.getUsername();
        log.info("{} user with username={}", desiredActive ? "Activating" : "Deactivating", username);

        if (user.isActive() == desiredActive) {
            String msg = desiredActive ? "User is already active" : "User is already inactive";
            log.warn("{} for username={}", msg, username);
            throw new ValidationException(msg);
        }

        user.setActive(desiredActive);
    }

    private User buildUser(UserProfileData request, String username, String rawPassword) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setActive(request.isActive());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        return user;
    }


}
