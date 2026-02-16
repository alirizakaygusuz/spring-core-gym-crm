package com.alirizakaygusuz.gymcrm.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Standard API response wrapper. Contains either data on success or error details on failure."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiStandardResponse<T>(

        @Schema(
                description = "Response payload. Present when the request is successful.",
                nullable = true,
                accessMode = Schema.AccessMode.READ_ONLY
        )
        T data,

        @Schema(
                description = "Error information. Present when the request fails.",
                nullable = true,
                accessMode = Schema.AccessMode.READ_ONLY
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
