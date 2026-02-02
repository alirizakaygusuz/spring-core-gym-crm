package com.alirizakaygusuz.gymcrm.dto.trainee;

import java.time.LocalDate;

public record TraineeProfileResponse(Long traineeId,
                                     String firstName,
                                     String lastName,
                                     boolean isActive,
                                     String username,
                                     LocalDate dateOfBirth,
                                     String address) {
}
