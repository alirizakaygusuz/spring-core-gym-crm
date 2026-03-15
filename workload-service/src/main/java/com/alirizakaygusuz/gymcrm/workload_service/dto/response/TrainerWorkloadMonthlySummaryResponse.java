package com.alirizakaygusuz.gymcrm.workload_service.dto.response;

public record  TrainerWorkloadMonthlySummaryResponse(
        Integer month,
        Integer totalTrainingDuration
) {
}
