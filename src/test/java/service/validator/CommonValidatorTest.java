package service.validator;

import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CommonValidatorTest {

    private CommonValidator commonValidator;

    @BeforeEach
    void setUp() {
        commonValidator = new CommonValidator();
    }

    @DisplayName("validateId should not throw exception when id is valid")
    @Test
    void validateId_shouldNotThrowExceptionWhenIdIsValid() {
        commonValidator.validateId(1L);
    }

    @DisplayName("validateId should throw IllegalArgumentException when id is invalid")
    @Test
    void validateId_shouldThrowExceptionWhenIdIsInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            commonValidator.validateId(null);
        });
    }

    @DisplayName("validateNotBlank should not throw exception when value is valid")
    @Test
    void validateNotBlank_shouldNotThrowExceptionWhenValueIsValid() {
        commonValidator.validateNotBlank("ValidValue", "TestField");
    }


    @DisplayName("validateNotBlank should throw IllegalArgumentException when value is invalid")
    @Test
    void validateNotBlank_shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            commonValidator.validateNotBlank(null, "TestField");
        });
    }




}
