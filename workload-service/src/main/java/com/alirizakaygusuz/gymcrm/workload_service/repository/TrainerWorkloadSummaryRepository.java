package com.alirizakaygusuz.gymcrm.workload_service.repository;

import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerWorkloadSummaryRepository extends JpaRepository<TrainerWorkloadSummary, Long> {

}
