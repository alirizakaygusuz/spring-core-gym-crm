package com.alirizakaygusuz.gymcrm.workload_service.service;

import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadSummaryResponse;

public interface TrainerWorkloadService {

    void processTrainerWorkload(TrainerWorkloadRequest request);

    TrainerWorkloadSummaryResponse getTrainerWorkloadSummary(String username, Integer year, Integer month);
}
