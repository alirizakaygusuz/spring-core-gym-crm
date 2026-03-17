package com.alirizakaygusuz.gymcrm.workload_service.dto.workload;

import java.util.List;

public record TrainerWorkloadSummaryResponse(
        String username,
        String firstName,
        String lastName,
        Boolean isActive,
        List<TrainerWorkloadYearlySummaryResponse> yearlySummaries

) {
}
