package com.alirizakaygusuz.gymcrm.workload_service.dto.workload;

public record  TrainerWorkloadMonthlySummaryResponse(
        Integer month,
        Integer totalTrainingDuration
) {
}
