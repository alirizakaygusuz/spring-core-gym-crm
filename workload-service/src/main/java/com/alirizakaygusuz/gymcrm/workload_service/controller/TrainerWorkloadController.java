package com.alirizakaygusuz.gymcrm.workload_service.controller;

import com.alirizakaygusuz.gymcrm.workload_service.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadSummaryResponse;
import com.alirizakaygusuz.gymcrm.workload_service.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Trainer Workload Microservice",
        description = "Operations related to managing and retrieving trainer workloads, including processing workload data and fetching monthly summaries."
)
public class TrainerWorkloadController extends BaseController {

    private final TrainerWorkloadService trainerWorkloadService;


    @Operation(
            summary = "Process Trainer Workload",
            description = "Endpoint to process the workload data for a trainer. This can include adding new workload entries or deleting existing ones based on the provided request data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workload processed successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient workload duration."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "404", description = "Trainer workload not found.")
    })
    @PostMapping
    public ResponseEntity<ApiStandardResponse<Void>> processTrainerWorkload(@RequestBody @Valid TrainerWorkloadRequest request) {
        trainerWorkloadService.processTrainerWorkload(request);
        return ok();
    }


    @Operation(
            summary = "Get Trainer Workload Summary",
            description = "Endpoint to retrieve the workload summary for a specific trainer for a given month and year. The summary includes total training duration and other relevant workload metrics."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workload summary retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "404", description = "Trainer workload not found.")
    })
    @GetMapping("/{username}/summary")
    public ResponseEntity<ApiStandardResponse<TrainerWorkloadSummaryResponse>> getTrainerWorkloadSummary(

            @Parameter(description = "The username of the trainer for whom the workload summary is being requested.", example = "trainer.jane")
            @PathVariable
            String username,

            @Parameter(description = "The year for which the workload summary is being requested. Must be a positive integer.", example = "2024")
            @RequestParam
            @Positive
            Integer year,

            @Parameter(description = "The month for which the workload summary is being requested. Must be a positive integer between 1 and 12.", example = "06")
            @RequestParam
            @Positive
            Integer month

    ) {

        return ok(trainerWorkloadService.getTrainerWorkloadSummary(username, year, month));
    }


}
