package com.alirizakaygusuz.gymcrm.dto.training;

import java.time.LocalDate;

public record TrainingResponse(
        Long trainingId,
        Long trainerId,
        Long traineeId,
        String trainingName,
        Long trainingTypeId,
        LocalDate trainingDate,
        Integer trainingDuration
) {}
