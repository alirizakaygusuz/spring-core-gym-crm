package com.alirizakaygusuz.gymcrm.service.validator;

import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validator for User entities.
 *
 * <p>This class provides methods to validate User objects and their attributes.</p>
 */
@Component
public class UserValidator {

    private CommonValidator commonValidator;

    @Autowired
    public void setCommonValidator(CommonValidator commonValidator) {
        this.commonValidator = commonValidator;
    }

    public void validateId(Long id) {
        commonValidator.validateId(id);
    }

    public void validateUsername(String username) {
        commonValidator.validateNotBlank(username, "Username");
    }

    public void validateRequiredUserNames(String firstName, String lastName) {
        commonValidator.validateNotBlank(firstName, "First name");
        commonValidator.validateNotBlank(lastName, "Last name");
    }

    public void validateUser(User user){
        if(user == null) {
            throw new ValidationException("User cannot be null");
        }
        validateRequiredUserNames(user.getFirstName(), user.getLastName());
    }
}
