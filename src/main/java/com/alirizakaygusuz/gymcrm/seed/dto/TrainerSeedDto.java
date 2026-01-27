package com.alirizakaygusuz.gymcrm.seed.dto;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

public record TrainerSeedDto(
        long id,
        String firstName,
        String lastName,
        boolean isActive,
        TrainingTypeCode specialization) {
}
