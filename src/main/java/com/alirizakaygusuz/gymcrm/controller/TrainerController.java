package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.security.self.SelfService;
import com.alirizakaygusuz.gymcrm.service.trainer.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Trainer Management",
        description = "Operations for managing trainer profiles and training sessions"
)
@SecurityRequirement(name = "customAuth")
public class TrainerController extends BaseController {

    private final TrainerService trainerService;

    @Operation(
            summary = "Register a new trainer",
            description = "Creates a new trainer profile and returns registration details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., username already exists)")
    })
    @PostMapping
    public ResponseEntity<ApiStandardResponse<TrainerRegisterResponse>> register(
            @Valid @RequestBody TrainerRegisterRequest request
    ) {
        return ok(trainerService.register(request));
    }

    @Operation(
            summary = "Get trainer profile by username",
            description = "Returns trainer profile details including specialization, assigned trainees (if applicable), and active status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}")
    @SelfService
    public ResponseEntity<ApiStandardResponse<TrainerProfileResponse>> getProfile(

            @Parameter(description = "Username of the trainer whose profile will be retrieved", example = "trainer.jane", required = true)
            @PathVariable
            @NotBlank String username
    ) {

        return ok(trainerService.getProfile(username));
    }

    @Operation(
            summary = "Update trainer profile",
            description = "Updates trainer profile details such as personal information and specialization (depending on request model)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PutMapping("/{username}")
    @SelfService
    public ResponseEntity<ApiStandardResponse<TrainerProfileUpdateResponse>> updateProfile(

            @Parameter(description = "Username of the trainer whose profile will be updated", example = "trainer.jane", required = true)
            @PathVariable @NotBlank String username,

            @Valid @RequestBody TrainerProfileUpdateRequest request
    ) {
        return ok(trainerService.updateProfile(username, request));
    }

    @Operation(
            summary = "Get trainer trainings with filters",
            description = "Returns training sessions for the specified trainer. Optional filters may include date range and trainee-related fields (depending on filter DTO)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}/trainings")
    @SelfService
    public ResponseEntity<ApiStandardResponse<List<TrainerTrainingFilterResponse>>> getTrainings(

            @Parameter(description = "Username of the trainer", example = "trainer.jane", required = true)
            @PathVariable @NotBlank String username,

            TrainerTrainingFilterRequest filters
    ) {
        return ok(trainerService.getTrainings(username, filters));
    }

    @Operation(
            summary = "Set trainer active status",
            description = "Activates or deactivates the trainer profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PatchMapping("/{username}/active-status")
    @SelfService
    public ResponseEntity<ApiStandardResponse<Void>> setActiveStatus(

            @Parameter(description = "Username of the trainer", example = "trainer.jane", required = true)
            @PathVariable @NotBlank String username,

            @Parameter(description = "New active status", example = "true", required = true)
            @RequestParam boolean isActive
    ) {

        trainerService.setActiveStatus(username, isActive);
        return ok();
    }
}