package com.alirizakaygusuz.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import java.util.List;

@Schema(
        name = "ApiError",
        description = "Standard error response object returned when an API request fails."
)
public record ApiError(

        @Schema(
                description = "Application-specific error code.",
                example = "VALIDATION_ERROR"
        )
        String code,

        @Schema(
                description = "Human-readable error message describing the failure.",
                example = "Validation failed"
        )
        String message,

        @Schema(
                description = "List of field-level validation errors. Present only when validation fails.",
                required = false,
                implementation = FieldError.class
        )
        List<FieldError> fieldErrors
) {

    public static ApiError simple(String code, String message) {
        return new ApiError(code, message, null);
    }

    public static ApiError validation(List<FieldError> fieldErrors) {
        return new ApiError("VALIDATION_ERROR", "Validation failed", fieldErrors);
    }
}
