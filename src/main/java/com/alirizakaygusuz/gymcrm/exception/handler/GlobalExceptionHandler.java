package com.alirizakaygusuz.gymcrm.exception.handler;

import com.alirizakaygusuz.gymcrm.dto.response.ApiError;
import com.alirizakaygusuz.gymcrm.dto.response.ApiResponse;
import com.alirizakaygusuz.gymcrm.dto.response.FieldError;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuth(AuthenticationFailedException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("AUTH_FAILED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleBeanValidation(MethodArgumentNotValidException ex) {
        log.warn("Bean validation error: {}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ApiError apiError = ApiError.validation(fieldErrors);
        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation error: {}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> new FieldError(
                        v.getPropertyPath() != null ? v.getPropertyPath().toString() : "param",
                        v.getMessage()
                ))
                .toList();

        ApiError apiError = ApiError.validation(fieldErrors);
        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("INTERNAL_ERROR", "Unexpected error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(apiError));
    }


}
