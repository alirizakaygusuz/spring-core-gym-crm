package com.alirizakaygusuz.gymcrm.service.training;

import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;

import java.util.List;

public interface TrainingService {

    void addTraining(String currentUsername,TrainingCreateRequest request);

    List<TrainingTypeResponse> getTrainingTypes();
}
