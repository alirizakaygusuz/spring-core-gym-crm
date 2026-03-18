package com.alirizakaygusuz.gymcrm.workload_service.service;

import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadSummaryResponse;
import com.alirizakaygusuz.gymcrm.workload_service.enums.ActionType;
import com.alirizakaygusuz.gymcrm.workload_service.exception.InsufficientTrainerWorkloadDurationException;
import com.alirizakaygusuz.gymcrm.workload_service.exception.TrainerWorkloadNotFoundException;
import com.alirizakaygusuz.gymcrm.workload_service.mapper.TrainerWorkloadMapper;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadMonthlySummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadSummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadYearlySummary;
import com.alirizakaygusuz.gymcrm.workload_service.monitoring.AppMetrics;
import com.alirizakaygusuz.gymcrm.workload_service.repository.TrainerWorkloadSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadSummaryRepository trainerWorkloadSummaryRepository;

    private final TrainerWorkloadMapper trainerWorkloadMapper;

    private final AppMetrics appMetrics;


    @Override
    @Transactional
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        ActionType actionType = request.actionType();

        switch (actionType) {
            case ADD -> addTrainerWorkloadSummary(request);
            case DELETE -> deleteTrainerWorkloadSummary(request);
            default -> log.warn("Unknown action type: {}", actionType);
        }

    }

    //Add TrainerWorkloadSummary
    private void addTrainerWorkloadSummary(TrainerWorkloadRequest request) {

        appMetrics.incrementTrainerWorkloadAddAttempts();

        log.info("Processing workload for trainer: {}, action: {}, date: {}, duration: {}",
                request.username(), request.actionType(), request.trainingDate(), request.trainingDuration());

        TrainerWorkloadSummary trainerSummary = findTrainerWorkloadSummaryOrCreate(request);

        int year = request.trainingDate().getYear();
        int month = request.trainingDate().getMonthValue();


        TrainerWorkloadYearlySummary yearlySummary = findTrainerWorkloadYearlySummaryOrCreate(trainerSummary, year);

        TrainerWorkloadMonthlySummary monthlySummary = findTrainerWorkloadMonthlySummaryOrCreate(yearlySummary, month);

        monthlySummary.setTotalTrainingDuration(monthlySummary.getTotalTrainingDuration() + request.trainingDuration());

        trainerWorkloadSummaryRepository.save(trainerSummary);
        appMetrics.incrementTrainerWorkloadAdd();

        log.info("Updated workload for trainer: {}, year: {}, month: {}, new total duration: {}",
                request.username(), year, month, monthlySummary.getTotalTrainingDuration());

    }

    private TrainerWorkloadSummary findTrainerWorkloadSummaryOrCreate(TrainerWorkloadRequest request) {
        return trainerWorkloadSummaryRepository
                .findByUsernameWithSummaries(request.username())
                .orElseGet(() -> {
                    TrainerWorkloadSummary newTrainer = new TrainerWorkloadSummary();
                    newTrainer.setUsername(request.username());
                    newTrainer.setFirstName(request.firstName());
                    newTrainer.setLastName(request.lastName());
                    newTrainer.setIsActive(request.isActive());
                    newTrainer.setYearlySummaries(new ArrayList<>());
                    return newTrainer;
                });
    }

    private TrainerWorkloadYearlySummary findTrainerWorkloadYearlySummaryOrCreate(TrainerWorkloadSummary trainerSummary, int year) {
        return trainerSummary.getYearlySummaries()
                .stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    TrainerWorkloadYearlySummary newYearlySummary = new TrainerWorkloadYearlySummary();
                    newYearlySummary.setYear(year);
                    newYearlySummary.setMonthlySummaries(new ArrayList<>());
                    trainerSummary.getYearlySummaries().add(newYearlySummary);
                    return newYearlySummary;
                });
    }

    private TrainerWorkloadMonthlySummary findTrainerWorkloadMonthlySummaryOrCreate(TrainerWorkloadYearlySummary yearlySummary, int month) {
        return yearlySummary.getMonthlySummaries()
                .stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    TrainerWorkloadMonthlySummary newMonthlySummary = new TrainerWorkloadMonthlySummary();
                    newMonthlySummary.setMonth(month);
                    newMonthlySummary.setTotalTrainingDuration(0);
                    yearlySummary.getMonthlySummaries().add(newMonthlySummary);
                    return newMonthlySummary;
                });
    }


    //Delete TrainerWorkloadSummary
    private void deleteTrainerWorkloadSummary(TrainerWorkloadRequest request) {

        appMetrics.incrementTrainerWorkloadDeleteAttempts();

        log.info("Processing workload deletion for trainer: {}, action: {}, date: {}, duration: {}",
                request.username(), request.actionType(), request.trainingDate(), request.trainingDuration());

        TrainerWorkloadSummary trainerWorkloadSummary = trainerWorkloadSummaryRepository
                .findByUsernameWithSummaries(request.username())
                .orElseThrow(() -> new TrainerWorkloadNotFoundException("Trainer with username " + request.username() + " not found"));

        int year = request.trainingDate().getYear();
        int month = request.trainingDate().getMonthValue();

        TrainerWorkloadYearlySummary yearlySummary = findTrainerWorkloadYearlyOrThrow(trainerWorkloadSummary, year);

        TrainerWorkloadMonthlySummary monthlySummary = findTrainerWorkloadMonthlyOrThrow(yearlySummary, month);

        int currentDuration = monthlySummary.getTotalTrainingDuration() - request.trainingDuration();

        if (currentDuration < 0) {
            throw new InsufficientTrainerWorkloadDurationException("Cannot delete training session. Current total duration for month " +
                    month + " in year " + year + " is less than the duration of the session being deleted.");
        } else {
            monthlySummary.setTotalTrainingDuration(currentDuration);
        }

        trainerWorkloadSummaryRepository.save(trainerWorkloadSummary);
        appMetrics.incrementTrainerWorkloadDelete();

        log.info("Updated workload after deletion for trainer: {}, year: {}, month: {}, new total duration: {}",
                request.username(), year, month, monthlySummary.getTotalTrainingDuration());

    }


    private TrainerWorkloadYearlySummary findTrainerWorkloadYearlyOrThrow(TrainerWorkloadSummary trainerWorkloadSummary, int year) {
        return trainerWorkloadSummary.getYearlySummaries()
                .stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseThrow(() -> new TrainerWorkloadNotFoundException("Yearly summary for year " + year + " not found for trainer " + trainerWorkloadSummary.getUsername()));

    }

    private TrainerWorkloadMonthlySummary findTrainerWorkloadMonthlyOrThrow(TrainerWorkloadYearlySummary yearlySummary, int month) {
        return yearlySummary.getMonthlySummaries()
                .stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> new TrainerWorkloadNotFoundException("Monthly summary for month " + month + " not found in year " +
                        yearlySummary.getYear()));
    }


    //Get TrainerWorkloadSummary
    @Override
    @Transactional(readOnly = true)
    public TrainerWorkloadSummaryResponse getTrainerWorkloadSummary(String username, Integer year, Integer month) {

        appMetrics.incrementTrainerWorkloadGetAttempts();

        log.info("Retrieving workload summary for trainer: {}, year: {}, month: {}", username, year, month);


        TrainerWorkloadSummary trainerWorkloadSummary = trainerWorkloadSummaryRepository.findByUsernameWithSummaries(username)
                .orElseThrow(() -> new TrainerWorkloadNotFoundException("Trainer with username " + username + " not found"
                ));


        TrainerWorkloadYearlySummary trainerWorkloadYearlySummary  = findTrainerWorkloadYearlyOrThrow(trainerWorkloadSummary, year);

        TrainerWorkloadMonthlySummary trainerWorkloadMonthlySummary = findTrainerWorkloadMonthlyOrThrow(trainerWorkloadYearlySummary, month);

        trainerWorkloadYearlySummary.setMonthlySummaries(List.of(trainerWorkloadMonthlySummary));
        trainerWorkloadSummary.setYearlySummaries(List.of(trainerWorkloadYearlySummary));

        appMetrics.incrementTrainerWorkloadGet();

        log.info("Retrieved workload summary for trainer: {}, year: {}, month: {}, total duration: {}",
                username, year, month, trainerWorkloadMonthlySummary.getTotalTrainingDuration());

        return trainerWorkloadMapper.toTrainerWorkloadSummaryResponse(trainerWorkloadSummary);
    }


}
