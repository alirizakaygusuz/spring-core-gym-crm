package com.alirizakaygusuz.gymcrm.workload_service.dto.workload;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "TrainerWorkloadYearlySummaryResponse",
        description = "Response object representing the yearly summary of a trainer's workload, including the year and a list of monthly summaries."
)
public record TrainerWorkloadYearlySummaryResponse(
        @Schema(
                description = "Year of the workload summary.",
                example = "2024"
        )
        Integer year,

        @Schema(
                description = "List of monthly summaries for the trainer's workload.",
                example = "[{ \"month\": 6, \"totalTrainingDuration\": 240 }]"
        )
        List<TrainerWorkloadMonthlySummaryResponse> monthlySummaries
) {
}
