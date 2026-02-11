package com.alirizakaygusuz.gymcrm.dto.trainer.profile;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

public record TrainerProfileSummaryResponse(
        String username,
        String firstName,
        String lastName,
        TrainingTypeCode specialization
) {
}
