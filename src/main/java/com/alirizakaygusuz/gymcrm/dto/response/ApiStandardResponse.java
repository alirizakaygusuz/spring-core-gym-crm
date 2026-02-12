package com.alirizakaygusuz.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ApiStandardResponse",
        description = "Standard API response wrapper. Contains either data on success or error details on failure."
)
public record ApiStandardResponse<T>(

        @Schema(
                description = "Response payload. Present when the request is successful.",
                required = false
        )
        T data,

        @Schema(
                description = "Error information. Present when the request fails.",
                required = false
        )
        ApiError error
) {

    public static <T> ApiStandardResponse<T> data(T payload) {
        return new ApiStandardResponse<>(payload, null);
    }

    public static ApiStandardResponse<Void> noBody() {
        return new ApiStandardResponse<>(null, null);
    }

    public static ApiStandardResponse<Void> error(ApiError error) {
        return new ApiStandardResponse<>(null, error);
    }
}
