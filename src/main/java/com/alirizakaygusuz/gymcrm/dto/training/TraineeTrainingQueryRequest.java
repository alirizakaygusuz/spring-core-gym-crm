package com.alirizakaygusuz.gymcrm.dto.training;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

import java.time.LocalDate;

public record TraineeTrainingQueryRequest(LocalDate from, LocalDate to, String trainerName,
                                          TrainingTypeCode trainingType) {
}
