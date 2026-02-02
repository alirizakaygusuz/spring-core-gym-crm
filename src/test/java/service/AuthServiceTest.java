package service;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.AuthService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
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
class AuthServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CommonValidator commonValidator;

    @InjectMocks
    private AuthService authService;

    @DisplayName("authenticateAndGetUser should return User when credentials are valid")
    @Test
    void authenticateAndGetUser_shouldReturnUserWhenCredentialsAreValid() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.username()).thenReturn("john.doe");
        when(request.password()).thenReturn("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");
        user.setPassword("ENC(password)");

        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "ENC(password)")).thenReturn(true);

        User result = authService.authenticateAndGetUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe", result.getUsername());

        verify(commonValidator).validateNotNull(request, "Login request");
        verify(commonValidator).validateNotBlank("john.doe", "Username");
        verify(commonValidator).validateNotBlank("password", "Password");

        verify(userDao).findByUsername("john.doe");
        verify(passwordEncoder).matches("password", "ENC(password)");

        verifyNoMoreInteractions(commonValidator, userDao, passwordEncoder);
    }

    @DisplayName("authenticateAndGetUser should throw AuthenticationFailedException when user not found")
    @Test
    void authenticateAndGetUser_shouldThrowAuthenticationFailedExceptionWhenUserNotFound() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.username()).thenReturn("john.doe");
        when(request.password()).thenReturn("password");

        when(userDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.authenticateAndGetUser(request)
        );

        assertEquals("Invalid username or password", ex.getMessage());

        verify(commonValidator).validateNotNull(request, "Login request");
        verify(commonValidator).validateNotBlank("john.doe", "Username");
        verify(commonValidator).validateNotBlank("password", "Password");

        verify(userDao).findByUsername("john.doe");

        verifyNoMoreInteractions(commonValidator, userDao);
        verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("authenticateAndGetUser should throw AuthenticationFailedException when password is invalid")
    @Test
    void authenticateAndGetUser_shouldThrowAuthenticationFailedExceptionWhenPasswordIsInvalid() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.username()).thenReturn("john.doe");
        when(request.password()).thenReturn("wrong");

        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("ENC(password)");

        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENC(password)")).thenReturn(false);

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.authenticateAndGetUser(request)
        );

        assertEquals("Invalid username or password", ex.getMessage());

        verify(commonValidator).validateNotNull(request, "Login request");
        verify(commonValidator).validateNotBlank("john.doe", "Username");
        verify(commonValidator).validateNotBlank("wrong", "Password");

        verify(userDao).findByUsername("john.doe");
        verify(passwordEncoder).matches("wrong", "ENC(password)");

        verifyNoMoreInteractions(commonValidator, userDao, passwordEncoder);
    }

    @DisplayName("authenticateAndGetUser should throw ValidationException when request is null")
    @Test
    void authenticateAndGetUser_shouldThrowValidationExceptionWhenRequestIsNull() {
        LoginRequest request = null;

        doThrow(new com.alirizakaygusuz.gymcrm.exception.ValidationException("Login request cannot be null"))
                .when(commonValidator).validateNotNull(request, "Login request");

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> authService.authenticateAndGetUser(request)
        );

        assertEquals("Login request cannot be null", ex.getMessage());

        verify(commonValidator).validateNotNull(null, "Login request");
        verifyNoMoreInteractions(commonValidator);

        verifyNoInteractions(userDao, passwordEncoder);
    }




}
