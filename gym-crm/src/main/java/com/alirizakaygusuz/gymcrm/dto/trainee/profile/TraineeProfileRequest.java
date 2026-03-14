package com.alirizakaygusuz.gymcrm.dto.trainee.profile;

import java.time.LocalDate;

public interface TraineeProfileRequest {
    LocalDate dateOfBirth();
    String address();
}
