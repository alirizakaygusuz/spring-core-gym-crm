package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.response.ApiResponse;
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
public class TraineeController extends BaseController {

    private final TraineeService traineeService;
    private final CurrentUserExtractor currentUserExtractor;

    @PostMapping
    public ResponseEntity<ApiResponse<TraineeRegisterResponse>> register(
            @Valid @RequestBody TraineeRegisterRequest request
    ) {
        return ok(traineeService.register(request));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<TraineeProfileResponse>> getProfile(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") @NotBlank String targetUsername
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.getProfile(currentUsername, targetUsername));
    }

    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<TraineeProfileUpdateResponse>> updateProfile(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") @NotBlank String targetUsername,
            @Valid @RequestBody TraineeProfileUpdateRequest request
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.updateProfile(currentUsername, targetUsername, request));
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(HttpServletRequest httpServletRequest,
                                                           @PathVariable(value = "username") @NotBlank String targetUsername) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        traineeService.deleteProfile(currentUsername, targetUsername);
        return ok();
    }

    @GetMapping("/{username}/trainers/not-assigned")
    public ResponseEntity<ApiResponse<List<TrainerProfileSummaryResponse>>> getNotAssignedActiveTrainers(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") @NotBlank String targetUsername
    ) {

        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.getNotAssignedActiveTrainers(currentUsername, targetUsername));
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<ApiResponse<List<TrainerProfileSummaryResponse>>> updateTrainerList(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") @NotBlank String targetUsername,
            @RequestBody @NotNull List<@NotBlank String> trainerUsernames
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.updateTrainerList(currentUsername, targetUsername, trainerUsernames));
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<ApiResponse<List<TraineeTrainingFilterResponse>>> getTrainings(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") @NotBlank String targetUsername,
            TraineeTrainingFilterRequest filters
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(traineeService.getTrainings(currentUsername, targetUsername, filters));
    }

    @PatchMapping("/{username}/active-status")
    public ResponseEntity<ApiResponse<Void>> setActiveStatus(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") @NotBlank String targetUsername,
            @RequestParam(value = "isActive") boolean isActive
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        traineeService.setActiveStatus(currentUsername, targetUsername, isActive);
        return ok();
    }


}
