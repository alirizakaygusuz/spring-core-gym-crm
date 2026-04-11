package com.alirizakaygusuz.gymcrm.workload_service.repository;

import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadSummaryRepository extends MongoRepository<TrainerWorkloadSummary, String> {


    Optional<TrainerWorkloadSummary> findByUsername(String username);


}
