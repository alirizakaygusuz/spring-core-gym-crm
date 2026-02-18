package com.alirizakaygusuz.gymcrm.service.training;

import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;

import java.util.List;

public interface TrainingService {

    void addTraining(TrainingCreateRequest request);

    List<TrainingTypeResponse> getTrainingTypes();
}
