package com.alirizakaygusuz.gymcrm.controller.api;

import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/trainees")
@Tag(
        name = "Trainee Management",
        description = "Operations for managing trainee profiles, trainers, and training sessions"
)
@SecurityRequirement(name = "customAuth")
public interface TraineeApi {

    @Operation(
            summary = "Register a new trainee",
            description = "Creates a new trainee profile and returns registration details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., username already exists)")
    })
    @PostMapping
    ResponseEntity<ApiStandardResponse<TraineeRegisterResponse>> register(
            @Valid @RequestBody TraineeRegisterRequest request
    );

    @Operation(
            summary = "Get trainee profile by username",
            description = "Returns trainee profile details including personal info, assigned trainers and active status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}")
    ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> getProfile(
            @Parameter(description = "Username of the trainee whose profile will be retrieved", example = "john.doe", required = true)
            @PathVariable @NotBlank String username
    );

    @Operation(
            summary = "Update trainee profile",
            description = "Updates trainee profile details. The request may include personal details and address fields."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/{username}")
    ResponseEntity<ApiStandardResponse<TraineeProfileUpdateResponse>> updateProfile(
            @Parameter(description = "Username of the trainee whose profile will be updated", example = "john.doe", required = true)
            @PathVariable @NotBlank String username,
            @Valid @RequestBody TraineeProfileUpdateRequest request
    );

    @Operation(
            summary = "Delete trainee profile",
            description = "Deletes the trainee profile and related associations."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    ResponseEntity<ApiStandardResponse<Void>> deleteProfile(
            @Parameter(description = "Username of the trainee whose profile will be deleted", example = "john.doe", required = true)
            @PathVariable @NotBlank String username
    );

    @Operation(
            summary = "Get active trainers not assigned to trainee",
            description = "Returns a list of active trainers who are not currently assigned to the specified trainee."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/trainers/not-assigned")
    ResponseEntity<ApiStandardResponse<List<TrainerProfileSummaryResponse>>> getNotAssignedActiveTrainers(
            @Parameter(description = "Username of the trainee", example = "john.doe", required = true)
            @PathVariable @NotBlank String username
    );

    @Operation(
            summary = "Update trainee's assigned trainers",
            description = "Replaces/updates the list of trainers assigned to a trainee using trainer usernames."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer list updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/{username}/trainers")
    ResponseEntity<ApiStandardResponse<List<TrainerProfileSummaryResponse>>> updateTrainerList(
            @Parameter(description = "Username of the trainee", example = "john.doe", required = true)
            @PathVariable @NotBlank String username,
            @Parameter(description = "List of trainer usernames to assign to the trainee", required = true, example = "[trainer.jane]")
            @RequestBody @NotNull List<@NotBlank String> trainerUsernames
    );

    @Operation(
            summary = "Get trainee trainings with filters",
            description = "Returns trainee training sessions. Optional filters may include date range and trainer-related fields."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/trainings")
    ResponseEntity<ApiStandardResponse<List<TraineeTrainingFilterResponse>>> getTrainings(
            @Parameter(description = "Username of the trainee", example = "john.doe", required = true)
            @PathVariable @NotBlank String username,
            TraineeTrainingFilterRequest filters
    );

    @Operation(
            summary = "Set trainee active status",
            description = "Activates or deactivates the trainee profile."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PatchMapping("/{username}/active-status")
    ResponseEntity<ApiStandardResponse<Void>> setActiveStatus(
            @Parameter(description = "Username of the trainee", example = "john.doe", required = true)
            @PathVariable @NotBlank String username,
            @Parameter(description = "New active status", example = "true", required = true)
            @RequestParam(value = "isActive") boolean isActive
    );
}