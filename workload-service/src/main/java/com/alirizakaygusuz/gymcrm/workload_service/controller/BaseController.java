package com.alirizakaygusuz.gymcrm.workload_service.controller;

import com.alirizakaygusuz.gymcrm.workload_service.dto.response.ApiStandardResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiStandardResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiStandardResponse.data(data));
    }

    protected ResponseEntity<ApiStandardResponse<Void>> ok() {
        return ResponseEntity.ok(ApiStandardResponse.noBody());
    }
}
