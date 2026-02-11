package com.alirizakaygusuz.gymcrm.dto.trainee.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TraineeRegisterRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address

) {
}
