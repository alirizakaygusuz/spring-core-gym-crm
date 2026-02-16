package com.alirizakaygusuz.gymcrm.dto.trainee.update;

import com.alirizakaygusuz.gymcrm.dto.common.UserUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(
        name = "TraineeProfileUpdateRequest",
        description = "Request body for updating a trainee profile, including personal details and active status."
)
public record TraineeProfileUpdateRequest(

        @Schema(
                description = "Trainee's first name.",
                example = "Johnathan",
                required = true
        )
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName,

        @Schema(
                description = "Trainee's last name.",
                example = "Johnson",
                required = true
        )
        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName,

        @Schema(
                description = "Trainee's date of birth (ISO-8601, yyyy-MM-dd).",
                example = "2000-01-01",
                required = false
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Schema(
                description = "Trainee's address.",
                example = "000 Main St, Anytown, USA",
                required = false
        )
        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @Schema(
                description = "Whether the trainee profile is active.",
                example = "true",
                required = true
        )
        @NotNull(message = "Active status is required")
        Boolean isActive

) implements UserUpdateRequest, TraineeProfileRequest {
}
