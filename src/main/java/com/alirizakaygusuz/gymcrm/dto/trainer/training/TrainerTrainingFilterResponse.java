package com.alirizakaygusuz.gymcrm.dto.trainer.training;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

import java.time.LocalDate;

public record TrainerTrainingFilterResponse(
        String trainingName,
        LocalDate trainingDate,
        TrainingTypeCode trainingType,
        int trainingDuration,
        String traineeName
) {
}
