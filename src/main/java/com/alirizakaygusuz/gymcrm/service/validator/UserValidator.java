package com.alirizakaygusuz.gymcrm.service.validator;

import com.alirizakaygusuz.gymcrm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private CommonValidator commonValidator;

    //Inject CommonValidator via setter injection
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

    private void validateRequiredUserNames(String firstName, String lastName) {
        commonValidator.validateNotBlank(firstName, "First name");
        commonValidator.validateNotBlank(lastName, "Last name");
    }

    public void validateUser(User user){
        if(user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        validateRequiredUserNames(user.getFirstName(), user.getLastName());
    }
}
