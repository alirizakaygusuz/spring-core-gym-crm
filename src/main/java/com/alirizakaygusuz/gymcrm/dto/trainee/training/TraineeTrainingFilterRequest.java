package com.alirizakaygusuz.gymcrm.dto.trainee.training;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


public record TraineeTrainingFilterRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate periodFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate periodTo,

        String trainerName,

        String trainingType
) {
}
