package com.alirizakaygusuz.gymcrm.service.validator;

import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Common validation utilities for service layer.
 *
 * <p>This class provides methods to validate common input parameters
 * such as IDs and string values.</p>
 */
@Component
public class CommonValidator {

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID must be a positive number");
        }
    }

    public void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be null or blank");
        }
    }

    public void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }



}
