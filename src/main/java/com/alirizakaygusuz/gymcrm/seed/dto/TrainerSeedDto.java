package com.alirizakaygusuz.gymcrm.seed.dto;

import com.alirizakaygusuz.gymcrm.model.TrainingType;

public record TrainerSeedDto(
        long id,
        String firstName,
        String lastName,
        boolean isActive,
        TrainingType specialization) {
}
