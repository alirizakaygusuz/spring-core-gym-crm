package service;

import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeProfileRequest;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.UserDomainService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private CommonValidator commonValidator;

    @InjectMocks
    private UserDomainService userDomainService;

    @DisplayName("createWithCredentials should return User when request is valid")
    @Test
    void createWithCredentials_shouldReturnUserWhenRequestIsValid() {
        TraineeProfileRequest request = new TraineeProfileRequest(
                "John",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "Address"
        );

        when(credentialsGenerator.generateUniqueUsername("John", "Doe"))
                .thenReturn("john.doe");
        when(credentialsGenerator.generateRandomPassword())
                .thenReturn("rawPass123");
        when(passwordEncoder.encode("rawPass123"))
                .thenReturn("ENC(rawPass123)");

        User user = userDomainService.createWithCredentials(request);

        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertTrue(user.isActive());
        assertEquals("john.doe", user.getUsername());
        assertEquals("ENC(rawPass123)", user.getPassword());

        verify(commonValidator).validateNotNull(request, "User profile creation request");
        verify(userValidator).validateRequiredUserNames("John", "Doe");
        verify(commonValidator).validateNotNull(true, "Active status");

        verify(credentialsGenerator).generateUniqueUsername("John", "Doe");
        verify(credentialsGenerator).generateRandomPassword();
        verify(passwordEncoder).encode("rawPass123");

        verifyNoMoreInteractions(commonValidator, userValidator, credentialsGenerator, passwordEncoder);
    }

    @DisplayName("createWithCredentials should throw ValidationException when request is null")
    @Test
    void createWithCredentials_shouldThrowValidationExceptionWhenRequestIsNull() {
        TraineeProfileRequest request = null;

        doThrow(new ValidationException("User profile creation request cannot be null"))
                .when(commonValidator).validateNotNull(request, "User profile creation request");

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userDomainService.createWithCredentials(request)
        );

        assertEquals("User profile creation request cannot be null", ex.getMessage());

        verify(commonValidator).validateNotNull(null, "User profile creation request");
        verifyNoMoreInteractions(commonValidator);
        verifyNoInteractions(userValidator, credentialsGenerator, passwordEncoder);
    }

    @DisplayName("applyUpdate should apply updates when inputs are valid")
    @Test
    void applyUpdate_shouldApplyUpdatesWhenInputsAreValid() {
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("Old");
        user.setLastName("Name");
        user.setActive(false);

        TraineeProfileRequest request = new TraineeProfileRequest(
                "John",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "Address"
        );

        userDomainService.applyUpdate(user, request);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertTrue(user.isActive());

        verify(commonValidator).validateNotNull(user, "User");
        verify(commonValidator).validateNotNull(request, "User profile creation request");
        verify(userValidator).validateRequiredUserNames("John", "Doe");
        verify(commonValidator).validateNotNull(true, "Active status");

        verifyNoMoreInteractions(commonValidator, userValidator);
        verifyNoInteractions(credentialsGenerator, passwordEncoder);
    }

    @DisplayName("changePassword should set encoded password when inputs are valid")
    @Test
    void changePassword_shouldSetEncodedPasswordWhenInputsAreValid() {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("old");

        when(passwordEncoder.encode("newPass"))
                .thenReturn("ENC(newPass)");

        userDomainService.changePassword(user, "newPass");

        assertEquals("ENC(newPass)", user.getPassword());

        verify(commonValidator).validateNotNull(user, "User");
        verify(commonValidator).validateNotBlank("newPass", "New password");
        verify(passwordEncoder).encode("newPass");

        verifyNoMoreInteractions(commonValidator, passwordEncoder);
        verifyNoInteractions(userValidator, credentialsGenerator);
    }

    @DisplayName("activate should set active=true when user is currently inactive")
    @Test
    void activate_shouldSetActiveTrueWhenUserIsCurrentlyInactive() {
        User user = new User();
        user.setUsername("john.doe");
        user.setActive(false);

        userDomainService.activate(user);

        assertTrue(user.isActive());

        verify(commonValidator).validateNotNull(user, "User");
        verifyNoMoreInteractions(commonValidator);
        verifyNoInteractions(userValidator, credentialsGenerator, passwordEncoder);
    }

    @DisplayName("deactivate should set active=false when user is currently active")
    @Test
    void deactivate_shouldSetActiveFalseWhenUserIsCurrentlyActive() {
        User user = new User();
        user.setUsername("john.doe");
        user.setActive(true);

        userDomainService.deactivate(user);

        assertFalse(user.isActive());

        verify(commonValidator).validateNotNull(user, "User");
        verifyNoMoreInteractions(commonValidator);
        verifyNoInteractions(userValidator, credentialsGenerator, passwordEncoder);
    }
}
