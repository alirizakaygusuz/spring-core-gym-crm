package com.alirizakaygusuz.gymcrm.controller.api;


import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v1/trainings")
@Tag(
        name = "Training Management",
        description = "Operations for creating trainings and retrieving training types"
)
@SecurityRequirement(name = "customAuth")
public interface TrainingApi {


    @Operation(
            summary = "Add training",
            description = "Creates a new training record for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    ResponseEntity<ApiStandardResponse<Void>> addTraining(
            @Valid @RequestBody TrainingCreateRequest request
    );


    @Operation(
            summary = "Get training types",
            description = "Returns the list of available training types."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    })
    @GetMapping("/types")
     ResponseEntity<ApiStandardResponse<List<TrainingTypeResponse>>> getTrainingTypes();
}
