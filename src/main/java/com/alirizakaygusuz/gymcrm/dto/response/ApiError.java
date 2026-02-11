package com.alirizakaygusuz.gymcrm.dto.response;


import java.util.List;


public record ApiError(String code,
                       String message,
                       List<FieldError> fieldErrors) {

    public static ApiError simple(String code, String message) {
        return new ApiError(code, message, null);
    }

    public static ApiError validation(List<FieldError> fieldErrors) {
        return new ApiError("VALIDATION_ERROR", "Validation failed", fieldErrors);
    }


}