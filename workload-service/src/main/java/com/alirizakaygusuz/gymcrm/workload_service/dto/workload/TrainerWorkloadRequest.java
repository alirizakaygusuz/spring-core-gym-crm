package com.alirizakaygusuz.gymcrm.workload_service.dto.workload;

import com.alirizakaygusuz.gymcrm.workload_service.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(
        name = "TrainerWorkloadRequest",
        description = "Request object for adding and deleting a trainer's workload entry."
)
public record TrainerWorkloadRequest(

        @Schema(
                description = "Username of the trainer whose workload entry is being added or deleted.",
                example = "jane.smith"
        )
        @NotBlank(message = "Username is required")
        String username,

        @Schema(
                description = "First name of the trainer.",
                example = "Jane"
        )
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(
                description = "Last name of the trainer.",
                example = "Smith"
        )
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(
                description = "Is the trainer active during this workload entry? True if active, false if not.",
                example = "true"
        )
        @NotNull(message = "Active status is required")
        Boolean isActive,


        @Schema(
                description = "Date of the training session (ISO-8601 format, yyyy-MM-dd).",
                example = "2024-07-01"
        )
        @NotNull
        LocalDate trainingDate,

        @Schema(
                description = "Duration of the training session in minutes. Must be a positive integer.",
                example = "60"
        )
        @NotNull(message = "Training duration is required")
        @Positive(message = "Training duration must be a positive integer")
        Integer trainingDuration,

        @Schema(
                description = "Type of action to perform on the workload entry. Allowed values: ADD, DELETE.",
                example = "ADD"
        )
        @NotNull(message = "Action type is required")
        ActionType actionType

) {
}
