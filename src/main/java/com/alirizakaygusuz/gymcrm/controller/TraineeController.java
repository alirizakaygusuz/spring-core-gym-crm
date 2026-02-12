package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.filter.CurrentUserExtractor;
import com.alirizakaygusuz.gymcrm.service.trainee.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Trainee Management",
        description = "Operations for managing trainee profiles, trainers, and training sessions"
)
@SecurityRequirement(name = "customAuth")
public class TraineeController extends BaseController {

    private final TraineeService traineeService;
    private final CurrentUserExtractor currentUserExtractor;

    @Operation(
            summary = "Register a new trainee",
            description = "Creates a new trainee profile and returns registration details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., username already exists)")
    })
    @PostMapping
    public ResponseEntity<ApiStandardResponse<TraineeRegisterResponse>> register(
            @Valid @RequestBody TraineeRegisterRequest request
    ) {
        return ok(traineeService.register(request));
    }

    @Operation(
            summary = "Get trainee profile by username",
            description = "Returns trainee profile details including personal info, assigned trainers and active status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> getProfile(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Parameter(description = "Username of the trainee whose profile will be retrieved", example = "john_doe", required = true)
            @PathVariable(value = "username")
            @NotBlank String targetUsername
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.getProfile(currentUsername, targetUsername));
    }

    @Operation(
            summary = "Update trainee profile",
            description = "Updates trainee profile details. The request may include personal details and address fields."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<ApiStandardResponse<TraineeProfileUpdateResponse>> updateProfile(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Parameter(description = "Username of the trainee whose profile will be updated", example = "john_doe", required = true)
            @PathVariable(value = "username")
            @NotBlank String targetUsername,

            @Valid @RequestBody TraineeProfileUpdateRequest request
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.updateProfile(currentUsername, targetUsername, request));
    }

    @Operation(
            summary = "Delete trainee profile",
            description = "Deletes the trainee profile and related associations."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiStandardResponse<Void>> deleteProfile(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Parameter(description = "Username of the trainee whose profile will be deleted", example = "john_doe", required = true)
            @PathVariable(value = "username")
            @NotBlank String targetUsername
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        traineeService.deleteProfile(currentUsername, targetUsername);
        return ok();
    }

    @Operation(
            summary = "Get active trainers not assigned to trainee",
            description = "Returns a list of active trainers who are not currently assigned to the specified trainee."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/trainers/not-assigned")
    public ResponseEntity<ApiStandardResponse<List<TrainerProfileSummaryResponse>>> getNotAssignedActiveTrainers(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Parameter(description = "Username of the trainee", example = "john_doe", required = true)
            @PathVariable(value = "username")
            @NotBlank String targetUsername
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.getNotAssignedActiveTrainers(currentUsername, targetUsername));
    }

    @Operation(
            summary = "Update trainee's assigned trainers",
            description = "Replaces/updates the list of trainers assigned to a trainee using trainer usernames."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer list updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/{username}/trainers")
    public ResponseEntity<ApiStandardResponse<List<TrainerProfileSummaryResponse>>> updateTrainerList(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Parameter(description = "Username of the trainee", example = "john_doe", required = true)
            @PathVariable(value = "username")
            @NotBlank String targetUsername,

            @Parameter(
                    description = "List of trainer usernames to assign to the trainee",
                    required = true,
                    example = "[\"trainer1\",\"trainer2\"]"
            )
            @RequestBody
            @NotNull List<@NotBlank String> trainerUsernames
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.updateTrainerList(currentUsername, targetUsername, trainerUsernames));
    }

    @Operation(
            summary = "Get trainee trainings with filters",
            description = "Returns trainee training sessions. Optional filters may include date range and trainer-related fields (depending on filter DTO)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<ApiStandardResponse<List<TraineeTrainingFilterResponse>>> getTrainings(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Parameter(description = "Username of the trainee", example = "john_doe", required = true)
            @PathVariable(value = "username")
            @NotBlank String targetUsername,

            TraineeTrainingFilterRequest filters
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.getTrainings(currentUsername, targetUsername, filters));
    }

    @Operation(
            summary = "Set trainee active status",
            description = "Activates or deactivates the trainee profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PatchMapping("/{username}/active-status")
    public ResponseEntity<ApiStandardResponse<Void>> setActiveStatus(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,

            @Parameter(description = "Username of the trainee", example = "john_doe", required = true)
            @PathVariable(value = "username")
            @NotBlank String targetUsername,

            @Parameter(description = "New active status", example = "true", required = true)
            @RequestParam(value = "isActive") boolean isActive
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        traineeService.setActiveStatus(currentUsername, targetUsername, isActive);
        return ok();
    }
}