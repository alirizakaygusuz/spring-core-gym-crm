package com.alirizakaygusuz.gymcrm.client;


import com.alirizakaygusuz.gymcrm.client.dto.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "workload-service",
        path = "/api/v1/workload/trainers",
        configuration = WorkloadServiceFeignConfig.class
)
public interface WorkloadServiceClient {

    Logger log = LoggerFactory.getLogger(WorkloadServiceClient.class);


    @PostMapping
    @CircuitBreaker(name = "workloadService", fallbackMethod = "processTrainerWorkloadFallback")
    ResponseEntity<ApiStandardResponse<Void>> processTrainerWorkload(@RequestBody TrainerWorkloadRequest request);

    default ResponseEntity<ApiStandardResponse<Void>> processTrainerWorkloadFallback(TrainerWorkloadRequest request, Throwable throwable) {
        log.warn("Workload service fallback triggered. trainerUsername={}, actionType={}, reason={}",
                request.username(), request.actionType(), throwable.getMessage());

        return ResponseEntity.ok(ApiStandardResponse.noBody());
    }


}
