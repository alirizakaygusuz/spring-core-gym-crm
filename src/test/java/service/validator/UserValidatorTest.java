package service.validator;

import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    private UserValidator userValidator;

    @Mock
    private CommonValidator commonValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
        userValidator.setCommonValidator(commonValidator);
    }

    @DisplayName("validateId should delegate to CommonValidator.validateId")
    @Test
    void validateId_shouldDelegateToCommonValidator() {
        Long id = 10L;

        assertDoesNotThrow(() -> userValidator.validateId(id));

        verify(commonValidator).validateId(id);
        verifyNoMoreInteractions(commonValidator);
    }

    @DisplayName("validateId should propagate ValidationException from CommonValidator")
    @Test
    void validateId_shouldPropagateException() {
        Long invalidId = -1L;
        doThrow(new ValidationException("ID must be a positive number"))
                .when(commonValidator).validateId(invalidId);

        ValidationException ex = assertThrows(ValidationException.class, () -> userValidator.validateId(invalidId));
        assertEquals("ID must be a positive number", ex.getMessage());

        verify(commonValidator).validateId(invalidId);
        verifyNoMoreInteractions(commonValidator);
    }

    @DisplayName("validateUsername should delegate to CommonValidator.validateNotBlank with fieldName=Username")
    @Test
    void validateUsername_shouldDelegateToCommonValidator() {
        String username = "john.doe";

        assertDoesNotThrow(() -> userValidator.validateUsername(username));

        verify(commonValidator).validateNotBlank(username, "Username");
        verifyNoMoreInteractions(commonValidator);
    }

    @DisplayName("validateRequiredUserNames should validate firstName and lastName via CommonValidator")
    @Test
    void validateRequiredUserNames_shouldDelegateToCommonValidator() {
        String firstName = "John";
        String lastName = "Doe";

        assertDoesNotThrow(() -> userValidator.validateRequiredUserNames(firstName, lastName));

        verify(commonValidator).validateNotBlank(firstName, "First name");
        verify(commonValidator).validateNotBlank(lastName, "Last name");
        verifyNoMoreInteractions(commonValidator);
    }

    @DisplayName("validateUser should throw ValidationException when user is null")
    @Test
    void validateUser_shouldThrow_whenUserIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> userValidator.validateUser(null));
        assertEquals("User cannot be null", ex.getMessage());

        verifyNoInteractions(commonValidator);
    }

    @DisplayName("validateUser should validate firstName and lastName using CommonValidator")
    @Test
    void validateUser_shouldValidateNames() {
        User user = mock(User.class);
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");

        assertDoesNotThrow(() -> userValidator.validateUser(user));

        verify(commonValidator).validateNotBlank("John", "First name");
        verify(commonValidator).validateNotBlank("Doe", "Last name");
        verifyNoMoreInteractions(commonValidator);
    }

    @DisplayName("validateUser should propagate ValidationException when firstName is invalid")
    @Test
    void validateUser_shouldPropagate_whenFirstNameInvalid() {
        User user = mock(User.class);
        when(user.getFirstName()).thenReturn("   ");
        when(user.getLastName()).thenReturn("Doe");

        doThrow(new ValidationException("First name cannot be null or blank"))
                .when(commonValidator).validateNotBlank("   ", "First name");

        ValidationException ex = assertThrows(ValidationException.class, () -> userValidator.validateUser(user));
        assertEquals("First name cannot be null or blank", ex.getMessage());

        verify(commonValidator).validateNotBlank("   ", "First name");
        verifyNoMoreInteractions(commonValidator);
    }
}
