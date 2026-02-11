package com.alirizakaygusuz.gymcrm.dto.trainee.update;

import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public record TraineeProfileUpdateResponse(
        String username,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String address,
        boolean isActive,
        List<TrainerProfileSummaryResponse> trainers
) {
}
