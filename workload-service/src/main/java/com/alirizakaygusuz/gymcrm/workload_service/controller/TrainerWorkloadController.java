package com.alirizakaygusuz.gymcrm.workload_service.controller;

import com.alirizakaygusuz.gymcrm.workload_service.controller.api.TrainerWorkloadApi;
import com.alirizakaygusuz.gymcrm.workload_service.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadSummaryResponse;
import com.alirizakaygusuz.gymcrm.workload_service.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Validated
public class TrainerWorkloadController extends BaseController implements TrainerWorkloadApi {

    private final TrainerWorkloadService trainerWorkloadService;


    @Override
    public ResponseEntity<ApiStandardResponse<Void>> processTrainerWorkload(@RequestBody @Valid TrainerWorkloadRequest request) {
        trainerWorkloadService.processTrainerWorkload(request);
        return ok();
    }



    @Override
    public ResponseEntity<ApiStandardResponse<TrainerWorkloadSummaryResponse>> getTrainerWorkloadSummary(
            @PathVariable String username,
            @RequestParam @Positive Integer year,
            @RequestParam @Positive Integer month) {
        return ok(trainerWorkloadService.getTrainerWorkloadSummary(username, year, month));
    }


}
