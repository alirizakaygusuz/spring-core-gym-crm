package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.controller.api.TrainingApi;
import com.alirizakaygusuz.gymcrm.dto.response.ApiStandardResponse;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.security.authorization.self.SelfTrainingService;
import com.alirizakaygusuz.gymcrm.service.training.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingController extends BaseController implements TrainingApi {

    private final TrainingService trainingService;

    @Override
    @SelfTrainingService
    public ResponseEntity<ApiStandardResponse<Void>> addTraining(
            @Valid @RequestBody TrainingCreateRequest request
    ) {


        trainingService.addTraining(request);
        return ok();
    }

    @Override
    public ResponseEntity<ApiStandardResponse<List<TrainingTypeResponse>>> getTrainingTypes() {
        return ok(trainingService.getTrainingTypes());
    }
}