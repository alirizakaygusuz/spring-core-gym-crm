package com.alirizakaygusuz.gymcrm.dto.trainer.update;

import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;

import java.util.List;

public record TrainerProfileUpdateResponse(
        String username,
        String firstName,
        String lastName,
        TrainingTypeCode specialization,
        boolean isActive,
        List<TraineeProfileSummaryResponse> trainees
) {
}
