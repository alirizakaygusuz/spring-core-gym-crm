package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.controller.api.TraineeApi;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.security.authorization.self.SelfTraineeService;
import com.alirizakaygusuz.gymcrm.service.trainee.TraineeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class TraineeController extends BaseController implements TraineeApi {

    private final TraineeService traineeService;

    @Override
    public ResponseEntity<ApiStandardResponse<TraineeRegisterResponse>> register(@Valid @RequestBody TraineeRegisterRequest request) {
        return ok(traineeService.register(request));
    }

    @Override
    @SelfTraineeService
    public ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> getProfile(@PathVariable @NotBlank String username) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("AUTH name={}, authorities={}", auth.getName(), auth.getAuthorities());
        return ok(traineeService.getProfile(username));
    }

    @Override
    @SelfTraineeService
    public ResponseEntity<ApiStandardResponse<TraineeProfileUpdateResponse>> updateProfile(@PathVariable @NotBlank String username, @Valid @RequestBody TraineeProfileUpdateRequest request) {
        return ok(traineeService.updateProfile(username, request));
    }

    @Override
    @SelfTraineeService
    public ResponseEntity<ApiStandardResponse<Void>> deleteProfile(@PathVariable @NotBlank String username) {
        traineeService.deleteProfile(username);
        return ok();
    }

    @Override
    @SelfTraineeService
    public ResponseEntity<ApiStandardResponse<List<TrainerProfileSummaryResponse>>> getNotAssignedActiveTrainers(@PathVariable @NotBlank String username) {
        return ok(traineeService.getNotAssignedActiveTrainers(username));
    }

    @Override
    @SelfTraineeService
    public ResponseEntity<ApiStandardResponse<List<TrainerProfileSummaryResponse>>> updateTrainerList(@PathVariable @NotBlank String username, @RequestBody @NotNull List<@NotBlank String> trainerUsernames) {
        return ok(traineeService.updateTrainerList(username, trainerUsernames));
    }

    @Override
    @SelfTraineeService
    public ResponseEntity<ApiStandardResponse<List<TraineeTrainingFilterResponse>>> getTrainings(@PathVariable @NotBlank String username, TraineeTrainingFilterRequest filters) {
        return ok(traineeService.getTrainings(username, filters));
    }

    @Override
    @SelfTraineeService
    public ResponseEntity<ApiStandardResponse<Void>> setActiveStatus(@PathVariable @NotBlank String username, @RequestParam(value = "isActive") boolean isActive) {
        traineeService.setActiveStatus(username, isActive);
        return ok();
    }
}