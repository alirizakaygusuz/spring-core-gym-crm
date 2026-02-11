package com.alirizakaygusuz.gymcrm.dto.response;

public record ApiResponse<T>(
        T data,
        ApiError error
) {
    public static <T> ApiResponse<T> data(T payload) {
        return new ApiResponse<>(payload, null);
    }

    public static ApiResponse<Void> noBody() {
        return new ApiResponse<>(null, null);
    }

    public static ApiResponse<Void> error(ApiError error) {
        return new ApiResponse<>(null, error);
    }
}