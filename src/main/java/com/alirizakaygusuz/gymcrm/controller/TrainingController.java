package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.filter.CurrentUserExtractor;
import com.alirizakaygusuz.gymcrm.service.training.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final CurrentUserExtractor currentUserExtractor;

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
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Valid @RequestBody TrainingCreateRequest request
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        trainingService.addTraining(currentUsername, request);
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