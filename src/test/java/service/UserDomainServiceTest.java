package service;

import com.alirizakaygusuz.gymcrm.dto.common.UserCreationResult;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.RoleType;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.role.RoleService;
import com.alirizakaygusuz.gymcrm.service.user.UserDomainService;
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

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserDomainService userDomainService;

    @Test
    @DisplayName("createWithCredentials should create user with generated credentials")
    void createWithCredentials_shouldCreateUserWithGeneratedCredentials() {
        TraineeRegisterRequest request = new TraineeRegisterRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St"
        );

        when(credentialsGenerator.generateUniqueUsername("John", "Doe"))
                .thenReturn("John.Doe");
        when(credentialsGenerator.generateRandomPassword())
                .thenReturn("rawPassword123");
        when(passwordEncoder.encode("rawPassword123"))
                .thenReturn("encodedPassword123");

        UserCreationResult userCreationResult = userDomainService.createWithCredentials(request , RoleType.TRAINER);
        User result = userCreationResult.user();

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("John.Doe", result.getUsername());
        assertEquals("encodedPassword123", result.getPassword());
        assertTrue(result.isActive());

        verify(credentialsGenerator).generateUniqueUsername("John", "Doe");
        verify(credentialsGenerator).generateRandomPassword();
        verify(passwordEncoder).encode("rawPassword123");
        verify(roleService).assignRoleToUser(result, RoleType.TRAINER);
        verifyNoMoreInteractions(credentialsGenerator, passwordEncoder);
        verifyNoInteractions(commonValidator, userValidator);
    }

    @Test
    @DisplayName("applyUpdate should update user fields from request")
    void applyUpdate_shouldUpdateUserFieldsFromRequest() {
        User user = new User();
        user.setUsername("John.Doe");
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");
        user.setActive(false);

        TraineeProfileUpdateRequest request = new TraineeProfileUpdateRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                true
        );

        userDomainService.applyUpdate(user, request);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertTrue(user.isActive());

        verify(commonValidator).validateNotNull(user, "User");
        verifyNoMoreInteractions(commonValidator);
        verifyNoInteractions(userValidator, credentialsGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("applyUpdate should throw ValidationException when user is null")
    void applyUpdate_shouldThrowValidationExceptionWhenUserIsNull() {
        TraineeProfileUpdateRequest request = new TraineeProfileUpdateRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                true
        );

        doThrow(new ValidationException("User cannot be null"))
                .when(commonValidator).validateNotNull(null, "User");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userDomainService.applyUpdate(null, request)
        );

        assertEquals("User cannot be null", exception.getMessage());

        verify(commonValidator).validateNotNull(null, "User");
        verifyNoMoreInteractions(commonValidator);
        verifyNoInteractions(userValidator, credentialsGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("changePassword should encode and set new password")
    void changePassword_shouldEncodeAndSetNewPassword() {
        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("oldPassword");

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("encodedNewPassword");

        userDomainService.changePassword(user, "newPassword123");

        assertEquals("encodedNewPassword", user.getPassword());

        verify(passwordEncoder).encode("newPassword123");
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoInteractions(commonValidator, userValidator, credentialsGenerator);
    }

    @Test
    @DisplayName("setActiveStatus should activate user when currently inactive")
    void setActiveStatus_shouldActivateUserWhenCurrentlyInactive() {
        User user = new User();
        user.setUsername("John.Doe");
        user.setActive(false);

        userDomainService.setActiveStatus(user, true);

        assertTrue(user.isActive());

        verifyNoInteractions(commonValidator, userValidator, credentialsGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("setActiveStatus should deactivate user when currently active")
    void setActiveStatus_shouldDeactivateUserWhenCurrentlyActive() {
        User user = new User();
        user.setUsername("John.Doe");
        user.setActive(true);

        userDomainService.setActiveStatus(user, false);

        assertFalse(user.isActive());

        verifyNoInteractions(commonValidator, userValidator, credentialsGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("setActiveStatus should throw ValidationException when user is already active")
    void setActiveStatus_shouldThrowValidationExceptionWhenUserIsAlreadyActive() {
        User user = new User();
        user.setUsername("John.Doe");
        user.setActive(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userDomainService.setActiveStatus(user, true)
        );

        assertEquals("User is already active", exception.getMessage());

        verifyNoInteractions(commonValidator, userValidator, credentialsGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("setActiveStatus should throw ValidationException when user is already inactive")
    void setActiveStatus_shouldThrowValidationExceptionWhenUserIsAlreadyInactive() {
        User user = new User();
        user.setUsername("John.Doe");
        user.setActive(false);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userDomainService.setActiveStatus(user, false)
        );

        assertEquals("User is already inactive", exception.getMessage());

        verifyNoInteractions(commonValidator, userValidator, credentialsGenerator, passwordEncoder);
    }
}