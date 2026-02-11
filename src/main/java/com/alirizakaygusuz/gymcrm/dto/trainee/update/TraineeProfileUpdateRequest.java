package com.alirizakaygusuz.gymcrm.dto.trainee.update;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record TraineeProfileUpdateRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @NotNull(message = "Active status is required")
        Boolean isActive

) {
}
