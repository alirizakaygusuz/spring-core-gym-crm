package com.alirizakaygusuz.gymcrm.workload_service.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiStandardResponse<T>(

        T data,

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
