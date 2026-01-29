package service;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.common.UserProfileData;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeProfileRequest;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.AuthService;
import com.alirizakaygusuz.gymcrm.service.UserDomainService;
import com.alirizakaygusuz.gymcrm.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserDao userDao;

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private UserService userService;

    @DisplayName("createUserWithCredentials should return saved User when request is valid")
    @Test
    void createUserWithCredentials_shouldReturnSavedUserWhenRequestIsValid() {
        UserProfileData request = new TraineeProfileRequest(
                "John",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "Address"
        );

        User builtUser = new User();
        builtUser.setFirstName("John");
        builtUser.setLastName("Doe");
        builtUser.setActive(true);
        builtUser.setUsername("john.doe");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setActive(true);
        savedUser.setUsername("john.doe");

        when(userDomainService.createWithCredentials(request)).thenReturn(builtUser);
        when(userDao.save(builtUser)).thenReturn(savedUser);

        User result = userService.createUserWithCredentials(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertTrue(result.isActive());
        assertEquals("john.doe", result.getUsername());

        verify(userDomainService).createWithCredentials(request);
        verify(userDao).save(builtUser);
        verifyNoMoreInteractions(userDomainService, userDao);
        verifyNoInteractions(authService);
    }

    @DisplayName("createUserWithCredentials should throw ValidationException when request is null")
    @Test
    void createUserWithCredentials_shouldThrowValidationExceptionWhenRequestIsNull() {
        UserProfileData request = null;

        doThrow(new ValidationException("Request cannot be null"))
                .when(userDomainService).createWithCredentials(request);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userService.createUserWithCredentials(request)
        );

        assertEquals("Request cannot be null", ex.getMessage());

        verify(userDomainService).createWithCredentials(null);
        verifyNoMoreInteractions(userDomainService);
        verifyNoInteractions(userDao, authService);
    }

    @DisplayName("changePassword should update User when inputs are valid")
    @Test
    void changePassword_shouldUpdateUserWhenInputsAreValid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");

        String newPassword = "newPass";

        assertDoesNotThrow(() -> userService.changePassword(user, newPassword));

        verify(userDomainService).changePassword(user, newPassword);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
        verifyNoInteractions(authService);
    }

    @DisplayName("changePassword should throw ValidationException when newPassword is invalid")
    @Test
    void changePassword_shouldThrowValidationExceptionWhenNewPasswordIsInvalid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");

        String newPassword = "   ";

        doThrow(new ValidationException("New password cannot be blank"))
                .when(userDomainService).changePassword(user, newPassword);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userService.changePassword(user, newPassword)
        );

        assertEquals("New password cannot be blank", ex.getMessage());

        verify(userDomainService).changePassword(user, newPassword);
        verifyNoMoreInteractions(userDomainService);
        verifyNoInteractions(userDao, authService);
    }

    @DisplayName("activate should update User when user is valid")
    @Test
    void activate_shouldUpdateUserWhenUserIsValid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");
        user.setActive(false);

        assertDoesNotThrow(() -> userService.activate(user));

        verify(userDomainService).activate(user);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
        verifyNoInteractions(authService);
    }

    @DisplayName("activate should throw ValidationException when user is already active")
    @Test
    void activate_shouldThrowValidationExceptionWhenUserIsAlreadyActive() {
        User user = new User();
        user.setUsername("john.doe");
        user.setActive(true);

        doThrow(new ValidationException("User is already active"))
                .when(userDomainService).activate(user);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userService.activate(user)
        );

        assertEquals("User is already active", ex.getMessage());

        verify(userDomainService).activate(user);
        verifyNoMoreInteractions(userDomainService);
        verifyNoInteractions(userDao, authService);
    }

    @DisplayName("deactivate should update User when user is valid")
    @Test
    void deactivate_shouldUpdateUserWhenUserIsValid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");
        user.setActive(true);

        assertDoesNotThrow(() -> userService.deactivate(user));

        verify(userDomainService).deactivate(user);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
        verifyNoInteractions(authService);
    }

    @DisplayName("deactivate should throw ValidationException when user is already inactive")
    @Test
    void deactivate_shouldThrowValidationExceptionWhenUserIsAlreadyInactive() {
        User user = new User();
        user.setUsername("john.doe");
        user.setActive(false);

        doThrow(new ValidationException("User is already inactive"))
                .when(userDomainService).deactivate(user);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userService.deactivate(user)
        );

        assertEquals("User is already inactive", ex.getMessage());

        verify(userDomainService).deactivate(user);
        verifyNoMoreInteractions(userDomainService);
        verifyNoInteractions(userDao, authService);
    }

    @DisplayName("authenticate should return authenticated User when login is valid")
    @Test
    void authenticate_shouldReturnAuthenticatedUserWhenLoginIsValid() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("john.doe");

        when(authService.authenticateAndGetUser(login)).thenReturn(authUser);

        User result = userService.authenticate(login);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe", result.getUsername());

        verify(authService).authenticateAndGetUser(login);
        verifyNoMoreInteractions(authService);
        verifyNoInteractions(userDao, userDomainService);
    }

    @DisplayName("authenticate should throw AuthenticationFailedException when login is invalid")
    @Test
    void authenticate_shouldThrowAuthenticationFailedExceptionWhenLoginIsInvalid() {
        LoginRequest login = mock(LoginRequest.class);

        doThrow(new AuthenticationFailedException("Invalid credentials"))
                .when(authService).authenticateAndGetUser(login);

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> userService.authenticate(login)
        );

        assertEquals("Invalid credentials", ex.getMessage());

        verify(authService).authenticateAndGetUser(login);
        verifyNoMoreInteractions(authService);
        verifyNoInteractions(userDao, userDomainService);
    }

    @DisplayName("applyProfileUpdate should update User when inputs are valid")
    @Test
    void applyProfileUpdate_shouldUpdateUserWhenInputsAreValid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");

        UserProfileData request = new TraineeProfileRequest(
                "John",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "Address"
        );

        assertDoesNotThrow(() -> userService.applyProfileUpdate(user, request));

        verify(userDomainService).applyUpdate(user, request);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
        verifyNoInteractions(authService);
    }

    @DisplayName("applyProfileUpdate should throw ValidationException when request is invalid")
    @Test
    void applyProfileUpdate_shouldThrowValidationExceptionWhenRequestIsInvalid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");

        UserProfileData request = new TraineeProfileRequest(
                " ",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "Address"
        );

        doThrow(new ValidationException("First and last name are required"))
                .when(userDomainService).applyUpdate(user, request);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userService.applyProfileUpdate(user, request)
        );

        assertEquals("First and last name are required", ex.getMessage());

        verify(userDomainService).applyUpdate(user, request);
        verifyNoMoreInteractions(userDomainService);
        verifyNoInteractions(userDao, authService);
    }
}
