package service.validator;

import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {

    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
        userValidator.setCommonValidator(new CommonValidator());
    }

    @DisplayName("validateId should call CommonValidator when id is valid")
    @Test
    void validateId_shouldCallCommonValidatorWhenIdIsValid() {
        Long validId = 1L;
        assertDoesNotThrow(() -> userValidator.validateId(validId));
    }

    @DisplayName("validateId should throw IllegalArgumentException when id is invalid")
    @Test
    void validateId_shouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;
        assertThrows(IllegalArgumentException.class, () -> {
            userValidator.validateId(invalidId);
        });

    }


    @DisplayName("validateUsername should call CommonValidator when username is valid")
    @Test
    void validateUsername_shouldCallCommonValidatorWhenUsernameIsValid() {
        String validUsername = "john.doe";
        assertDoesNotThrow(() -> userValidator.validateUsername(validUsername));

    }

    @DisplayName("validateUsername should throw IllegalArgumentException when username is blank")
    @Test
    void validateUsername_shouldThrowExceptionWhenUsernameIsBlank() {
        String blankUsername = "   ";
        assertThrows(IllegalArgumentException.class, () -> {
            userValidator.validateUsername(blankUsername);
        });
    }


    @DisplayName("validateUser should not throw exception when user is valid")
    @Test
    void validateUser_shouldNotThrowExceptionWhenUserIsValid() {
        Trainee validUser = new Trainee();
        validUser.setFirstName("John");
        validUser.setLastName("Doe");

        assertDoesNotThrow(() -> userValidator.validateUser(validUser));

    }

    @DisplayName("validateUser should throw IllegalArgumentException when user is null")
    @Test
    void validateUser_shouldThrowExceptionWhenUserIsNull() {
        Trainee nullUser = null;
        assertThrows(IllegalArgumentException.class, () -> {
            userValidator.validateUser(nullUser);
        });
    }


    @DisplayName("validateUser should throw IllegalArgumentException when first name is blank")
    @Test
    void validateUser_shouldThrowExceptionWhenFirstNameIsInvalid() {
        Trainee userWithBlankFirstName = new Trainee();
        userWithBlankFirstName.setFirstName("   ");
        userWithBlankFirstName.setLastName("Doe");

        assertThrows(IllegalArgumentException.class, () -> {
            userValidator.validateUser(userWithBlankFirstName);
        });
    }

    @DisplayName("validateUser should throw IllegalArgumentException when last name is blank")
    @Test
    void validateUser_shouldThrowExceptionWhenLastNameIsInvalid() {
        Trainee userWithBlankLastName = new Trainee();
        userWithBlankLastName.setFirstName("John");
        userWithBlankLastName.setLastName(null);

        assertThrows(IllegalArgumentException.class, () -> {
            userValidator.validateUser(userWithBlankLastName);
        });
    }


}
