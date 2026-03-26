package com.alirizakaygusuz.gymcrm.workload_service.dto.workload;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "TrainerWorkloadMonthlySummaryResponse",
        description = "Response object representing the monthly summary of a trainer's workload, including the month and total training duration."
)
public record  TrainerWorkloadMonthlySummaryResponse(

        @Schema(
                description = "Month of the workload summary in YYYY-MM format.",
                example = "2024-06"
        )
        Integer month,

        @Schema(
                description = "Total training duration for the month in minutes.",
                example = "240"
        )
        Integer totalTrainingDuration
) {
}
