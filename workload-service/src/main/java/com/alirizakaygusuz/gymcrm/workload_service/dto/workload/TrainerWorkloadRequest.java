package com.alirizakaygusuz.gymcrm.workload_service.dto.workload;

import com.alirizakaygusuz.gymcrm.workload_service.enums.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record TrainerWorkloadRequest(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotNull(message = "Active status is required")
        Boolean isActive,

        @NotNull
        LocalDate trainingDate,

        @NotNull(message = "Training duration is required")
        @Positive(message = "Training duration must be a positive integer")
        Integer trainingDuration,

        @NotNull(message = "Action type is required")
        ActionType actionType

) {
}
