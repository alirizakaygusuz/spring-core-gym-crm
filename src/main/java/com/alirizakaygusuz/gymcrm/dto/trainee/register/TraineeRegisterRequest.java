package com.alirizakaygusuz.gymcrm.dto.trainee.register;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        name = "TraineeRegisterRequest",
        description = "Request body for registering a new trainee, including personal details and profile information."
)
public record TraineeRegisterRequest(

        @Schema(
                description = "Trainee's first name.",
                example = "John",
                required = true
        )
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName,

        @Schema(
                description = "Trainee's last name.",
                example = "Doe",
                required = true
        )
        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName,

        @Schema(
                description = "Trainee's date of birth (ISO-8601, yyyy-MM-dd).",
                example = "1990-01-01",
                required = false
        )
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Schema(
                description = "Trainee's address.",
                example = "123 Main St, Anytown, USA",
                required = false
        )
        @Size(max = 255, message = "Address must be at most 255 characters")
        String address

) implements UserRegisterRequest, TraineeProfileRequest {
}
