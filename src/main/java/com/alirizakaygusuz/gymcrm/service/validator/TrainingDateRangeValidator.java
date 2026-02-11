package com.alirizakaygusuz.gymcrm.service.validator;

import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TrainingDateRangeValidator {

    public void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ValidationException("'from' date must be less than or equal to 'to' date");
        }
    }
}
