package com.alirizakaygusuz.gymcrm.service.training;

import com.alirizakaygusuz.gymcrm.messaging.dto.ActionType;
import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.messaging.TrainerWorkloadMessageProducer;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.util.WorkloadRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingDao trainingDao;

    private final TrainerWorkloadMessageProducer trainerWorkloadMessageProducer;



    @Override
    @Transactional
    public void addTraining(
            TrainingCreateRequest request
    ) {

        log.info("Add training requested by user-trainerName={} , trainingName={}",
                 request.trainerUsername(), request.trainingName());

        Trainee trainee = traineeDao.findByUsername(request.traineeUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Trainee", "username", request.traineeUsername()));

        Trainer trainer = trainerDao.findByUsername(request.trainerUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "username", request.trainerUsername()));

        TrainingType trainingType = trainingTypeDao.findByCode(TrainingTypeCode.fromString(request.trainingName()))
                .orElseThrow(() -> new ResourceNotFoundException("TrainingType", "name", request.trainingName()));

        Training training = buildTrainingForCreate(request, trainee, trainer, trainingType);

        Training savedTraining = trainingDao.save(training);

        log.info("Training created. id={}, traineeId={}, trainerId={}, typeId={}",
                savedTraining.getId(), trainee.getId(), trainer.getId(), trainingType.getId());


        trainerWorkloadMessageProducer.sendTrainerWorkloadEvent(WorkloadRequestBuilder.from(training , ActionType.ADD));

    }


    private Training buildTrainingForCreate(
            TrainingCreateRequest request,
            Trainee trainee, Trainer trainer,
            TrainingType trainingType
    ) {
        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingName(request.trainingName());
        training.setTrainingDate(request.trainingDate());
        training.setTrainingDuration(request.trainingDuration());
        return training;
    }


    @Override
    @Transactional(readOnly = true)
    public List<TrainingTypeResponse> getTrainingTypes() {
        log.info("Fetching all training types");
        return trainingTypeDao.findAll().stream()
                .map(t -> new TrainingTypeResponse(
                        t.getId(),
                        t.getTrainingTypeName()
                ))
                .toList();
    }


}