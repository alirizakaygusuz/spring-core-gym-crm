package com.alirizakaygusuz.gymcrm.workload_service.mapper;

import com.alirizakaygusuz.gymcrm.workload_service.config.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadMonthlySummaryResponse;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadSummaryResponse;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadYearlySummaryResponse;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadMonthlySummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadSummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadYearlySummary;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface TrainerWorkloadMapper {


    TrainerWorkloadSummaryResponse toTrainerWorkloadSummaryResponse(TrainerWorkloadSummary trainerWorkloadSummary);


    TrainerWorkloadYearlySummaryResponse toTrainerWorkloadYearlySummary(TrainerWorkloadYearlySummary trainerWorkloadYearlySummary);

    TrainerWorkloadMonthlySummaryResponse toTrainerWorkloadMonthlySummary(TrainerWorkloadMonthlySummary trainerWorkloadMonthlySummary);



}
