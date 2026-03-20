package com.alirizakaygusuz.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(
        name = "ApiError",
        description = "Standard error response object returned when an API request fails."
)
public record ApiError(

        @Schema(
                description = "Unique identifier for the request, useful for tracing and debugging.",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        String requestId,

        @Schema(
                description = "URN identifying the type of error.",
                example = "urn:com.alirizakaygusuz.gymcrm:error:validation"
        )
        String urn,

        @Schema(
                description = "Timestamp indicating when the error occurred.",
                example = "2024-06-01T12:00:00Z"
        )
        Instant timestamp,


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


        @ArraySchema(
                schema = @Schema(
                        description = "List of field-specific validation errors. Present only for validation failures.",
                        implementation = FieldError.class
                )
        )
        List<FieldError> fieldErrors

) {



    public static ApiError simple(
            String requestId,
            String endpointUrn,
            String code,
            String message
    ) {
        return new ApiError(
                requestId,
                endpointUrn,
                Instant.now(),
                code,
                message,
                null
        );
    }


    public static ApiError validation(
            String requestId,
            String endpointUrn,
            List<FieldError> fieldErrors
    ) {
        return new ApiError(
                requestId,
                endpointUrn,
                Instant.now(),
                "VALIDATION_ERROR",
                "Validation failed",
                fieldErrors
        );
    }

}
