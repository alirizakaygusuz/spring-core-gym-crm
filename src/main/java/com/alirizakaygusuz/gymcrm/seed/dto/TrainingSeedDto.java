package com.alirizakaygusuz.gymcrm.seed.dto;

import com.alirizakaygusuz.gymcrm.model.TrainingType;

import java.time.LocalDate;


public record TrainingSeedDto(
        long id,
        long traineeId,
        long trainerId,
        String trainingName,
        TrainingType trainingType,
        LocalDate trainingDate,
        int trainingDuration
) {}