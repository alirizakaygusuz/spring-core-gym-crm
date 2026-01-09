package com.alirizakaygusuz.gymcrm.service.validator;

import org.springframework.stereotype.Component;

@Component
public class CommonValidator {

    public void validateId(Long id) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number");
        }
    }

    public void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
    }

}
