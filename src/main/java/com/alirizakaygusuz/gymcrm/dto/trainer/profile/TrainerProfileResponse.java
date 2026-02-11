package com.alirizakaygusuz.gymcrm.dto.trainer.profile;

import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

import java.util.List;

public record TrainerProfileResponse(
        String firstName,
        String lastName,
        TrainingTypeCode specialization,
        boolean isActive,
        List<TraineeProfileSummaryResponse> trainees
) {
}
