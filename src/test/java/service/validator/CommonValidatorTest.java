package service.validator;

import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonValidatorTest {

    private CommonValidator commonValidator;

    @BeforeEach
    void setUp() {
        commonValidator = new CommonValidator();
    }

    @DisplayName("validateId should not throw when id is positive")
    @Test
    void validateId_shouldNotThrow_whenIdIsPositive() {
        assertDoesNotThrow(() -> commonValidator.validateId(1L));
        assertDoesNotThrow(() -> commonValidator.validateId(999L));
    }

    @DisplayName("validateId should throw ValidationException when id is null")
    @Test
    void validateId_shouldThrow_whenIdIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> commonValidator.validateId(null));
        assertEquals("ID must be a positive number", ex.getMessage());
    }

    @DisplayName("validateId should throw ValidationException when id is zero")
    @Test
    void validateId_shouldThrow_whenIdIsZero() {
        ValidationException ex = assertThrows(ValidationException.class, () -> commonValidator.validateId(0L));
        assertEquals("ID must be a positive number", ex.getMessage());
    }

    @DisplayName("validateId should throw ValidationException when id is negative")
    @Test
    void validateId_shouldThrow_whenIdIsNegative() {
        ValidationException ex = assertThrows(ValidationException.class, () -> commonValidator.validateId(-5L));
        assertEquals("ID must be a positive number", ex.getMessage());
    }

    @DisplayName("validateNotBlank should not throw when value is non-blank")
    @Test
    void validateNotBlank_shouldNotThrow_whenValueIsNonBlank() {
        assertDoesNotThrow(() -> commonValidator.validateNotBlank("abc", "Field"));
        assertDoesNotThrow(() -> commonValidator.validateNotBlank("  abc  ", "Field"));
    }

    @DisplayName("validateNotBlank should throw ValidationException when value is null")
    @Test
    void validateNotBlank_shouldThrow_whenValueIsNull() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> commonValidator.validateNotBlank(null, "TestField")
        );
        assertEquals("TestField cannot be null or blank", ex.getMessage());
    }

    @DisplayName("validateNotBlank should throw ValidationException when value is blank")
    @Test
    void validateNotBlank_shouldThrow_whenValueIsBlank() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> commonValidator.validateNotBlank("   ", "TestField")
        );
        assertEquals("TestField cannot be null or blank", ex.getMessage());
    }

    @DisplayName("validateNotNull should not throw when value is not null")
    @Test
    void validateNotNull_shouldNotThrow_whenValueIsNotNull() {
        assertDoesNotThrow(() -> commonValidator.validateNotNull(new Object(), "Obj"));
        assertDoesNotThrow(() -> commonValidator.validateNotNull("x", "Str"));
    }

    @DisplayName("validateNotNull should throw ValidationException when value is null")
    @Test
    void validateNotNull_shouldThrow_whenValueIsNull() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> commonValidator.validateNotNull(null, "TestField")
        );
        assertEquals("TestField cannot be null", ex.getMessage());
    }
}
