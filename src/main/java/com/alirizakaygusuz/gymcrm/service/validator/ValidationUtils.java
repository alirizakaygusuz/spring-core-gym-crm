package com.alirizakaygusuz.gymcrm.service.validator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ValidationUtils {

    public boolean validateDateRange(LocalDate from, LocalDate to) {
        return from == null || to == null || !from.isAfter(to);
    }
}
