package com.alirizakaygusuz.gymcrm.dto.trainee.training;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

import java.time.LocalDate;

public record TraineeTrainingFilterResponse(
        String trainingName,
        LocalDate trainingDate,
        TrainingTypeCode trainingType,
        int trainingDuration,
        String trainerName

) {
}
