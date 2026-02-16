package com.alirizakaygusuz.gymcrm.exception.handler;

import com.alirizakaygusuz.gymcrm.dto.response.ApiError;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.response.FieldError;
import com.alirizakaygusuz.gymcrm.exception.AuthorizationFailedException;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleAuth(AuthenticationFailedException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "AUTHENTICATION_FAILED",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiStandardResponse<Void>> handleBadRequest(Exception ex) {

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "BAD_REQUEST",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(ApiStandardResponse.error(apiError));
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleDomainNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "RESOURCE_NOT_FOUND",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleStaticNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        String uri = request.getRequestURI();

        if (isSwaggerPath(uri)) {
            log.debug("Swagger resource requested: {}", uri);
            return null;
        }

        log.warn("Static resource not found: {}", uri);

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "RESOURCE_NOT_FOUND",
                "Requested resource not found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error(apiError));
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleNoHandler(
            NoHandlerFoundException ex,
            HttpServletRequest request
    ) {
        String uri = request.getRequestURI();


        if (isSwaggerPath(uri)) {
            log.debug("Swagger resource requested: {}", uri);
            return null;
        }

        log.warn("No handler found for request: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "ENDPOINT_NOT_FOUND",
                "Requested endpoint not found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleAccessDenied(AuthorizationFailedException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "AUTHORIZATION_FAILED",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleCustomValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "VALIDATION_ERROR",
                ex.getMessage()
        );

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

        ApiError apiError = ApiError.validation(
                getRequestId(),
                buildDynamicUrn(),
                fieldErrors
        );

        return ResponseEntity.badRequest()
                .body(ApiStandardResponse.error(apiError));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex
    ) {
        log.warn("Method not allowed: {}", ex.getMessage());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "METHOD_NOT_ALLOWED",
                ex.getMessage()
        );

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

        ApiError apiError = ApiError.validation(
                getRequestId(),
                buildDynamicUrn(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(ApiStandardResponse.error(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleUnexpected(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiStandardResponse.error(apiError));
    }

    private String getRequestId() {
        String requestId = MDC.get("transactionId");
        return requestId != null ? requestId : UUID.randomUUID().toString();
    }


    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        return attributes.getRequest();
    }

    private String buildDynamicUrn() {
        HttpServletRequest request = getCurrentRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String endpoint = extractEndPoint(uri);

        return String.format("urn:com.alirizakaygusuz.gymcrm:api:%s:%s", method, endpoint);

    }

    private String extractEndPoint(String uri) {
        //remove /api
        String path = uri.replaceFirst("^/api/?", "");

        if (path.isBlank()) {
            return "unknown";
        }

        int slashIndex = path.indexOf('/');
        if (slashIndex == -1) {
            return path;  // v1
        }

        // v1/trainees/john.doe → v1:trainees
        String version = path.substring(0, slashIndex);  // v1
        String restPath = path.substring(slashIndex + 1);  // trainees/john.doe

        String endpoint;

        int secondSlash = restPath.indexOf('/');
        if (secondSlash == -1) {
            endpoint = restPath;
        } else {
            endpoint = restPath.substring(0, secondSlash);
        }

        return version + ":" + endpoint;
    }

    private boolean isSwaggerPath(String uri) {
        return uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/webjars/swagger-ui");
    }
}