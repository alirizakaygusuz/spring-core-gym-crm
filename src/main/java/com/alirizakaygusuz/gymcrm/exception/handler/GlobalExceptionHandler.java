package com.alirizakaygusuz.gymcrm.exception.handler;

import com.alirizakaygusuz.gymcrm.dto.response.ApiError;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.response.FieldError;
import com.alirizakaygusuz.gymcrm.exception.AccessDeniedException;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleAuth(AuthenticationFailedException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("AUTH_FAILED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiStandardResponse<Void>> handleBadRequest(Exception ex) {
        ApiError apiError = ApiError.simple("BAD_REQUEST", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiStandardResponse.error(apiError));
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleDomainNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleStaticNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        String uri = request.getRequestURI();

        // Swagger UI ve OpenAPI path'lerini ignore et - Spring'in default handler'ı handle etsin
        if (isSwaggerPath(uri)) {
            log.debug("Swagger resource requested: {}", uri);
            // Exception'ı re-throw etmek yerine null dön - Spring default handler devreye girer
            return null;
        }

        log.warn("Static resource not found: {}", uri);
        ApiError apiError = ApiError.simple("NOT_FOUND", "Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error(apiError));
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleNoHandler(
            NoHandlerFoundException ex,
            HttpServletRequest request
    ) {
        String uri = request.getRequestURI();

        // Swagger UI ve OpenAPI path'lerini ignore et
        if (isSwaggerPath(uri)) {
            log.debug("Swagger resource requested: {}", uri);
            return null;
        }

        log.warn("No handler found for request: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        ApiError apiError = ApiError.simple("NOT_FOUND", "Endpoint not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("ACCESS_DENIED", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleCustomValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        log.warn("Bean validation error: {}", ex.getMessage());

        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ApiError apiError = ApiError.validation(fieldErrors);
        return ResponseEntity.badRequest()
                .body(ApiStandardResponse.error(apiError));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex
    ) {
        log.warn("Method not allowed: {}", ex.getMessage());
        ApiError apiError = ApiError.simple("METHOD_NOT_ALLOWED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation error: {}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> new FieldError(
                        v.getPropertyPath() != null ? v.getPropertyPath().toString() : "param",
                        v.getMessage()
                ))
                .toList();

        ApiError apiError = ApiError.validation(fieldErrors);
        return ResponseEntity.badRequest().body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleUnexpected(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ApiError apiError = ApiError.simple("INTERNAL_SERVER_ERROR", "Unexpected server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiStandardResponse.error(apiError));
    }

    /**
     * Swagger UI ve OpenAPI documentation path'lerini kontrol eder
     */
    private boolean isSwaggerPath(String uri) {
        return uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/webjars/swagger-ui");
    }
}