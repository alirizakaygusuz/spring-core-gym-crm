package com.alirizakaygusuz.gymcrm.workload_service.exception.handler;


import com.alirizakaygusuz.gymcrm.workload_service.dto.response.ApiError;
import com.alirizakaygusuz.gymcrm.workload_service.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.workload_service.dto.response.FieldError;
import com.alirizakaygusuz.gymcrm.workload_service.exception.InsufficientTrainerWorkloadDurationException;
import com.alirizakaygusuz.gymcrm.workload_service.exception.TrainerWorkloadNotFoundException;
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

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TrainerWorkloadNotFoundException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleWorkloadNotFoundException(TrainerWorkloadNotFoundException ex) {
        log.warn("Workload not found: {}", ex.getMessage());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "WORKLOAD_NOT_FOUND",
                ex.getMessage()
        );


        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error(apiError));
    }


    @ExceptionHandler(InsufficientTrainerWorkloadDurationException.class)
    public ResponseEntity<ApiStandardResponse<Void>> handleInsufficientWorkloadDuration(InsufficientTrainerWorkloadDurationException ex) {
        log.warn("Insufficient workload duration: {}", ex.getMessage());

        ApiError apiError = ApiError.simple(
                getRequestId(),
                buildDynamicUrn(),
                "INSUFFICIENT_WORKLOAD_DURATION",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiStandardResponse.error(apiError));
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

        return String.format("urn:com.alirizakaygusuz.gymcrm.workload_service:api:%s:%s", method, endpoint);

    }

    private String extractEndPoint(String uri) {
        //remove /api
        String path = uri.replaceFirst("^/api/?", "");

        if (path.isBlank()) {
            return "unknown";
        }

        int slashIndex = path.indexOf('/');
        if (slashIndex == -1) {
            return path;
        }

        String version = path.substring(0, slashIndex);
        String restPath = path.substring(slashIndex + 1);

        String endpoint;

        int secondSlash = restPath.indexOf('/');
        if (secondSlash == -1) {
            endpoint = restPath;
        } else {
            endpoint = restPath.substring(0, secondSlash);
        }

        return version + ":" + endpoint;
    }
}
