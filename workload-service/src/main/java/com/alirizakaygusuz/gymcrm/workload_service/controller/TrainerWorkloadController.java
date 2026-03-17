package com.alirizakaygusuz.gymcrm.workload_service.controller;

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
@RequestMapping("/api/v1/workload/trainers")
@RequiredArgsConstructor
@Validated
public class TrainerWorkloadController extends BaseController {

    private final TrainerWorkloadService trainerWorkloadService;

    @PostMapping
    public ResponseEntity<ApiStandardResponse<Void>> processTrainerWorkload(@RequestBody @Valid TrainerWorkloadRequest request) {
        trainerWorkloadService.processTrainerWorkload(request);
        return ok();
    }

    @GetMapping("/{username}/summary")
    public ResponseEntity<ApiStandardResponse<TrainerWorkloadSummaryResponse>> getTrainerWorkloadSummary(
            @PathVariable String username,
            @RequestParam @Positive Integer year,
            @RequestParam @Positive Integer month

    ) {

        return ok(trainerWorkloadService.getTrainerWorkloadSummary(username, year, month));
    }



}
