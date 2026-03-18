package com.alirizakaygusuz.gymcrm.workload_service.dto.workload;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(
        name = "TrainerWorkloadSummaryResponse",
        description = "Response object representing the overall summary of a trainer's workload, including personal information and yearly summaries."
)
public record TrainerWorkloadSummaryResponse(

        @Schema(
                description = "Username of the trainer.",
                example = "jane.smith"
        )
        String username,

        @Schema(
                description = "First name of the trainer.",
                example = "Jane"
        )
        String firstName,

        @Schema(
                description = "Last name of the trainer.",
                example = "Smith"
        )
        String lastName,


        @Schema(
                description = "Indicates whether the trainer is currently active.",
                example = "true"
        )
        Boolean isActive,


        @Schema(
                description = "List of yearly summaries for the trainer's workload.",
                example = "[{ \"year\": 2024, \"monthlySummaries\": [{ \"month\": 6, \"totalTrainingDuration\": 240 }] }]"
        )
        List<TrainerWorkloadYearlySummaryResponse> yearlySummaries

) {
}
