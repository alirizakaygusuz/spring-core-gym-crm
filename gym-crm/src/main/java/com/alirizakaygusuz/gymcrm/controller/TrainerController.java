package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.controller.api.TrainerApi;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.security.authorization.self.SelfTrainerService;
import com.alirizakaygusuz.gymcrm.service.trainer.TrainerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class TrainerController extends BaseController implements TrainerApi {

    private final TrainerService trainerService;

    @Override
    public ResponseEntity<ApiStandardResponse<TrainerRegisterResponse>> register(@Valid @RequestBody TrainerRegisterRequest request) {
        return ok(trainerService.register(request));
    }

    @Override
    @SelfTrainerService
    public ResponseEntity<ApiStandardResponse<TrainerProfileResponse>> getProfile(@PathVariable @NotBlank String username) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("AUTH name={}, authorities={}", auth.getName(), auth.getAuthorities());
        return ok(trainerService.getProfile(username));
    }

    @Override
    @SelfTrainerService
    public ResponseEntity<ApiStandardResponse<TrainerProfileUpdateResponse>> updateProfile(@PathVariable @NotBlank String username, @Valid @RequestBody TrainerProfileUpdateRequest request) {
        return ok(trainerService.updateProfile(username, request));
    }

    @Override
    @SelfTrainerService
    public ResponseEntity<ApiStandardResponse<List<TrainerTrainingFilterResponse>>> getTrainings(@PathVariable @NotBlank String username, TrainerTrainingFilterRequest filters) {
        return ok(trainerService.getTrainings(username, filters));
    }

    @Override
    @SelfTrainerService
    public ResponseEntity<ApiStandardResponse<Void>> setActiveStatus(@PathVariable @NotBlank String username, @RequestParam boolean isActive) {
        trainerService.setActiveStatus(username, isActive);
        return ok();
    }
}