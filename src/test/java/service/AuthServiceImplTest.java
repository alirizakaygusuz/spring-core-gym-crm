package service;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.exception.AccessDeniedException;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.auth.AuthServiceImpl;
import com.alirizakaygusuz.gymcrm.service.validator.SelfAccessValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SelfAccessValidator selfAccessValidator;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("login should succeed when credentials are valid")
    void login_shouldSucceedWhenCredentialsAreValid() {
        String username = "john.doe";
        String password = "password123";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        assertDoesNotThrow(() -> authService.login(username, password));

        verify(userDao).findByUsername(username);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verifyNoMoreInteractions(userDao, passwordEncoder);
        verifyNoInteractions(selfAccessValidator);
    }

    @Test
    @DisplayName("login should throw AuthenticationFailedException when user not found")
    void login_shouldThrowAuthenticationFailedExceptionWhenUserNotFound() {
        String username = "john.doe";
        String password = "password123";

        when(userDao.findByUsername(username)).thenReturn(Optional.empty());

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login(username, password)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(userDao).findByUsername(username);
        verifyNoMoreInteractions(userDao);
        verifyNoInteractions(passwordEncoder, selfAccessValidator);
    }

    @Test
    @DisplayName("login should throw AuthenticationFailedException when password is incorrect")
    void login_shouldThrowAuthenticationFailedExceptionWhenPasswordIsIncorrect() {
        String username = "john.doe";
        String password = "wrongPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login(username, password)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(userDao).findByUsername(username);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verifyNoMoreInteractions(userDao, passwordEncoder);
        verifyNoInteractions(selfAccessValidator);
    }

    @Test
    @DisplayName("changePassword should update password when valid request and access allowed")
    void changePassword_shouldUpdatePasswordWhenValidRequestAndAccessAllowed() {
        String currentUsername = "john.doe";
        ChangePasswordRequest request = new ChangePasswordRequest(
                "john.doe",
                "oldPassword",
                "newPassword123"
        );

        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("oldEncodedPassword");

        when(userDao.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(request.newPassword())).thenReturn("newEncodedPassword");

        assertDoesNotThrow(() -> authService.changePassword(currentUsername, request));

        assertEquals("newEncodedPassword", user.getPassword());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.username());
        verify(userDao).findByUsername(request.username());
        verify(passwordEncoder).encode(request.newPassword());
        verify(userDao).update(user);
        verifyNoMoreInteractions(selfAccessValidator, userDao, passwordEncoder);
    }

    @Test
    @DisplayName("changePassword should throw AccessDeniedException when trying to change another user's password")
    void changePassword_shouldThrowAccessDeniedExceptionWhenTryingToChangeAnotherUsersPassword() {
        String currentUsername = "john.doe";
        ChangePasswordRequest request = new ChangePasswordRequest(
                "jane.doe",
                "oldPassword",
                "newPassword123"
        );

        doThrow(new AccessDeniedException("You can only access your own profile."))
                .when(selfAccessValidator).assertSelfAccess(currentUsername, request.username());

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> authService.changePassword(currentUsername, request)
        );

        assertEquals("You can only access your own profile.", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.username());
        verifyNoMoreInteractions(selfAccessValidator);
        verifyNoInteractions(userDao, passwordEncoder);
    }

    @Test
    @DisplayName("changePassword should throw AuthenticationFailedException when user not found")
    void changePassword_shouldThrowAuthenticationFailedExceptionWhenUserNotFound() {
        String currentUsername = "john.doe";
        ChangePasswordRequest request = new ChangePasswordRequest(
                "john.doe",
                "oldPassword",
                "newPassword123"
        );

        when(userDao.findByUsername(request.username())).thenReturn(Optional.empty());

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.changePassword(currentUsername, request)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.username());
        verify(userDao).findByUsername(request.username());
        verifyNoMoreInteractions(selfAccessValidator, userDao);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("authenticate should return true when credentials are valid")
    void authenticate_shouldReturnTrueWhenCredentialsAreValid() {
        String username = "john.doe";
        String password = "password123";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        boolean result = authService.authenticate(username, password);

        assertTrue(result);

        verify(userDao).findByUsername(username);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verifyNoMoreInteractions(userDao, passwordEncoder);
        verifyNoInteractions(selfAccessValidator);
    }

    @Test
    @DisplayName("authenticate should return false when password is incorrect")
    void authenticate_shouldReturnFalseWhenPasswordIsIncorrect() {
        String username = "john.doe";
        String password = "wrongPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        boolean result = authService.authenticate(username, password);

        assertFalse(result);

        verify(userDao).findByUsername(username);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verifyNoMoreInteractions(userDao, passwordEncoder);
        verifyNoInteractions(selfAccessValidator);
    }

    @Test
    @DisplayName("authenticate should throw AuthenticationFailedException when user not found")
    void authenticate_shouldThrowAuthenticationFailedExceptionWhenUserNotFound() {
        String username = "john.doe";
        String password = "password123";

        when(userDao.findByUsername(username)).thenReturn(Optional.empty());

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.authenticate(username, password)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(userDao).findByUsername(username);
        verifyNoMoreInteractions(userDao);
        verifyNoInteractions(passwordEncoder, selfAccessValidator);
    }
}