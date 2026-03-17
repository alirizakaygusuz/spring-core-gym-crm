package com.alirizakaygusuz.gymcrm.workload_service.dto.response;

import java.time.Instant;
import java.util.List;

public record ApiError(

        String requestId,

        String urn,

        Instant timestamp,

        String code,

        String message,

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
