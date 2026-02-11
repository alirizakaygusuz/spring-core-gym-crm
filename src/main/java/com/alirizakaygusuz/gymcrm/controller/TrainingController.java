package com.alirizakaygusuz.gymcrm.controller;

import com.alirizakaygusuz.gymcrm.dto.response.ApiResponse;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.filter.CurrentUserExtractor;
import com.alirizakaygusuz.gymcrm.service.training.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController extends BaseController{

    private final TrainingService trainingService;
    private final CurrentUserExtractor currentUserExtractor;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addTraining(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody TrainingCreateRequest request){

        String currentUsername =currentUserExtractor.currentUser(httpServletRequest);
        trainingService.addTraining(currentUsername , request);
        return ok();
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<TrainingTypeResponse>>> getTrainingTypes(
    ){
        return ok(trainingService.getTrainingTypes());
     }

}
