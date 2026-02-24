package service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.ChangePasswordRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginResponse;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.monitoring.metrics.AppMetrics;
import com.alirizakaygusuz.gymcrm.security.jwt.JwtService;
import com.alirizakaygusuz.gymcrm.service.auth.AuthServiceImpl;
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
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AppMetrics appMetrics;


    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    @DisplayName("login should succeed when credentials are valid")
    void login_shouldSucceedWhenCredentialsAreValid() {
        String username = "john.doe";
        String password = "password123";

        LoginRequest request = new LoginRequest(username, password);

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(username)).thenReturn("token123");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token123", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600000L, response.expiresIn());

        verify(userDao).findByUsername(username);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verify(jwtService).generateToken(username);
        verify(jwtService).getExpirationTime();
        verifyNoMoreInteractions(userDao, passwordEncoder, jwtService);
        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    @DisplayName("login should throw AuthenticationFailedException when user not found")
    void login_shouldThrowAuthenticationFailedExceptionWhenUserNotFound() {
        String username = "john.doe";
        String password = "password123";

        LoginRequest request = new LoginRequest(username, password);

        when(userDao.findByUsername(username)).thenReturn(Optional.empty());

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(userDao).findByUsername(username);
        verifyNoMoreInteractions(userDao);
        verifyNoInteractions(passwordEncoder, jwtService, traineeDao, trainerDao);
    }

    @Test
    @DisplayName("login should throw AuthenticationFailedException when password is incorrect")
    void login_shouldThrowAuthenticationFailedExceptionWhenPasswordIsIncorrect() {
        String username = "john.doe";
        String password = "wrongPassword";

        LoginRequest request = new LoginRequest(username, password);

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(userDao).findByUsername(username);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verifyNoMoreInteractions(userDao, passwordEncoder);
        verifyNoInteractions(jwtService, traineeDao, trainerDao);
    }

    @Test
    @DisplayName("changePassword should update password when valid request")
    void changePassword_shouldUpdatePasswordWhenValidRequest() {
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

        assertDoesNotThrow(() -> authService.changePassword(request));

        assertEquals("newEncodedPassword", user.getPassword());

        verify(userDao).findByUsername(request.username());
        verify(passwordEncoder).encode(request.newPassword());
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDao, passwordEncoder);
        verifyNoInteractions(jwtService, traineeDao, trainerDao);
    }

    @Test
    @DisplayName("changePassword should throw AuthenticationFailedException when user not found")
    void changePassword_shouldThrowAuthenticationFailedExceptionWhenUserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "john.doe",
                "oldPassword",
                "newPassword123"
        );

        when(userDao.findByUsername(request.username())).thenReturn(Optional.empty());

        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.changePassword(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(userDao).findByUsername(request.username());
        verifyNoMoreInteractions(userDao);
        verifyNoInteractions(passwordEncoder, jwtService, traineeDao, trainerDao);
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
        verifyNoInteractions(jwtService, traineeDao, trainerDao);
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
        verifyNoInteractions(jwtService, traineeDao, trainerDao);
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
        verifyNoInteractions(passwordEncoder, jwtService, traineeDao, trainerDao);
    }
}
