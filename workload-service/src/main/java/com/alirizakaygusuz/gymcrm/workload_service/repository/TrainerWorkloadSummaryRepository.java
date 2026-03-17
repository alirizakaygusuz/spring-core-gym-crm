package com.alirizakaygusuz.gymcrm.workload_service.repository;

import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadSummaryRepository extends JpaRepository<TrainerWorkloadSummary, Long> {

    @Query("""
            SELECT t FROM TrainerWorkloadSummary t
            LEFT JOIN FETCH t.yearlySummaries y
            LEFT JOIN FETCH y.monthlySummaries m
            WHERE t.username = :username
            """)
    Optional<TrainerWorkloadSummary> findByUsernameWithSummaries(String username);


}
