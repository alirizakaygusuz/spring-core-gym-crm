package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.response.ApiResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.filter.CurrentUserExtractor;
import com.alirizakaygusuz.gymcrm.service.trainer.TrainerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Validated
public class TrainerController extends  BaseController{

    private final TrainerService trainerService;
    private final CurrentUserExtractor currentUserExtractor;

    @PostMapping
    public ResponseEntity<ApiResponse<TrainerRegisterResponse>> register(
            @Valid @RequestBody TrainerRegisterRequest request
    ) {
        return ok(trainerService.register(request));
    }


    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<TrainerProfileResponse>> getProfile(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") String targetUsername
    ) {
        String currentUsername =currentUserExtractor.currentUser(httpServletRequest);
        return ok(trainerService.getProfile(currentUsername, targetUsername));
    }

    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<TrainerProfileUpdateResponse>> updateProfile(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") String targetUsername,
            @Valid @RequestBody TrainerProfileUpdateRequest request
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(trainerService.updateProfile(currentUsername, targetUsername, request));
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<ApiResponse<List<TrainerTrainingFilterResponse>>> getTrainings(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") String targetUsername,
            TrainerTrainingFilterRequest filters
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        return ok(trainerService.getTrainings(currentUsername, targetUsername, filters));
    }

    @PatchMapping("/{username}/active-status")
    public ResponseEntity<ApiResponse<Void>> setActiveStatus(
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "username") String targetUsername,
            @RequestParam boolean isActive
    ) {
        String currentUsername = currentUserExtractor.currentUser(httpServletRequest);
        trainerService.setActiveStatus(currentUsername, targetUsername, isActive);
        return ok();
    }

}
