package com.alirizakaygusuz.gymcrm.dto.training;

import java.time.LocalDate;

public record TrainerTrainingQueryRequest(LocalDate from, LocalDate to, String traineeName) {
}
