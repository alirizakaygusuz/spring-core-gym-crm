package com.alirizakaygusuz.gymcrm.seed.dto;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

import java.time.LocalDate;


public record TrainingSeedDto(
        long id,
        long traineeId,
        long trainerId,
        String trainingName,
        TrainingTypeCode trainingType,
        LocalDate trainingDate,
        int trainingDuration
) {}