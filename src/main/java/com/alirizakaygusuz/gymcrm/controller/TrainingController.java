package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.security.self.SelfService;
import com.alirizakaygusuz.gymcrm.service.training.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.alirizakaygusuz.gymcrm.controller.ControllerAuthUtils.verifyUserAccess;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
@Tag(
        name = "Training Management",
        description = "Operations for creating trainings and retrieving training types"
)
@SecurityRequirement(name = "customAuth")
public class TrainingController extends BaseController {

    private final TrainingService trainingService;

    @Operation(
            summary = "Add training",
            description = "Creates a new training record for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    public ResponseEntity<ApiStandardResponse<Void>> addTraining(
            @Valid @RequestBody TrainingCreateRequest request
    ) {

        verifyUserAccess(request.trainerUsername());
        trainingService.addTraining(request);
        return ok();
    }

    @Operation(
            summary = "Get training types",
            description = "Returns the list of available training types."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    })
    @GetMapping("/types")
    public ResponseEntity<ApiStandardResponse<List<TrainingTypeResponse>>> getTrainingTypes() {
        return ok(trainingService.getTrainingTypes());
    }
}