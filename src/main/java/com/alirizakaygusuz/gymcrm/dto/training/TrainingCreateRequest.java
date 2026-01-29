package com.alirizakaygusuz.gymcrm.dto.training;

import java.time.LocalDate;

public record TrainingCreateRequest(
        Long trainerId,
        Long trainingTypeId,
        String trainingName,
        LocalDate trainingDate,
        Integer trainingDuration
) {}
