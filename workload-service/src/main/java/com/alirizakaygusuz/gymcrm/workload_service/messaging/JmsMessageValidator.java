package com.alirizakaygusuz.gymcrm.workload_service.messaging;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsMessageValidator {

    private final Validator validator;

    public <T> void validate(T payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(payload);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed for message: {} | violations: {}", payload, message);
            throw new IllegalArgumentException("Invalid message: " + message);
        }
    }
}
