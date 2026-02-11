package com.alirizakaygusuz.gymcrm.dto.trainee.profile;

import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public record TraineeProfileResponse(
                                     String firstName,
                                     String lastName,
                                     LocalDate dateOfBirth,
                                     String address,
                                     boolean isActive,
                                     List<TrainerProfileSummaryResponse> trainers
) {
}
