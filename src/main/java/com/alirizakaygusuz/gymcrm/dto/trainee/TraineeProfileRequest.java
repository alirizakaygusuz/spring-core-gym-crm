package com.alirizakaygusuz.gymcrm.dto.trainee;

import com.alirizakaygusuz.gymcrm.dto.common.UserProfileData;

import java.time.LocalDate;

public record TraineeProfileRequest (String firstName,
                                   String lastName,
                                   Boolean isActive,
                                   LocalDate dateOfBirth,
                                   String address)  implements UserProfileData{
}