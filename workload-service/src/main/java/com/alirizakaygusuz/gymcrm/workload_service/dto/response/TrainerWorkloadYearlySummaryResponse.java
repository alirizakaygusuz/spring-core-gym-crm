package com.alirizakaygusuz.gymcrm.workload_service.dto.response;

import java.util.List;

public record TrainerWorkloadYearlySummaryResponse(
        Integer year,
        List<TrainerWorkloadMonthlySummaryResponse> monthlySummaries
) {
}
