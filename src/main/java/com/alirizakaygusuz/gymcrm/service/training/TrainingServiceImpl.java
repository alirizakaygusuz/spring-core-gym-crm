package com.alirizakaygusuz.gymcrm.service.training;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.mapper.TrainingMapper;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.service.user.UserServiceImpl;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingServiceImpl {

    private final UserServiceImpl userServiceImpl;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingDao trainingDao;
    private final CommonValidator commonValidator;
    private final TrainingMapper trainingMapper;


    @Transactional
    public TrainingResponse addTraining(LoginRequest request, TrainingCreateRequest createRequest) {
        commonValidator.validateNotNull(request, "Login request");
        validateTrainingRequest(createRequest);

        User authUser = userServiceImpl.authenticate(request);
        log.info("Add training requested by user={} , trainerId={}, trainingTypeId={}",
                authUser.getUsername(), createRequest.trainerId(), createRequest.trainingTypeId());

        Trainee trainee = traineeDao.findByUsername(authUser.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException("Only trainees can add trainings"));

        Trainer trainer = trainerDao.findById(createRequest.trainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", createRequest.trainerId()));

        TrainingType trainingType = trainingTypeDao.findById(createRequest.trainingTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("TrainingType", "id", createRequest.trainingTypeId()));


        Training training = buildTrainingForCreate(createRequest, trainee, trainer, trainingType);

        Training savedTraining = trainingDao.save(training);

        log.info("Training created. id={}, traineeId={}, trainerId={}, typeId={}",
                savedTraining.getId(), trainee.getId(), trainer.getId(), trainingType.getId());

        return trainingMapper.toCreateResponse(savedTraining);
    }


    @Transactional(readOnly = true)
    public List<TrainingResponse> getTraineeTrainings(
            LoginRequest login,
            TraineeTrainingQueryRequest query
    ) {
        commonValidator.validateNotNull(query, "Trainee training query request");
        validateDateRange(query.from(), query.to());
        User authUser = userServiceImpl.authenticate(login);

        traineeDao.findByUsername(authUser.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException("Only trainee can access trainee trainings list"));

        List<Training> trainings = trainingDao.findTraineeTrainingsByCriteria(
                authUser.getUsername(),
                query.from(),
                query.to(),
                query.trainerName(),
                query.trainingType()
        );


        return mapToTrainingResponses(trainings);

    }


    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainerTrainings(
            LoginRequest login,
            TrainerTrainingQueryRequest query
    ) {
        commonValidator.validateNotNull(query, "Trainer training query request");
        validateDateRange(query.from(), query.to());

        User authUser = userServiceImpl.authenticate(login);

        trainerDao.findByUsername(authUser.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException("Only trainer can access trainer trainings list"));

        List<Training> trainings = trainingDao.findTrainerTrainingsByCriteria(
                authUser.getUsername(),
                query.from(),
                query.to(),
                query.traineeName()
        );

        return mapToTrainingResponses(trainings);

    }

    private List<TrainingResponse> mapToTrainingResponses(List<Training> trainings) {
        List<TrainingResponse> responses = new ArrayList<>(trainings.size());
        for (Training t : trainings) {
            responses.add(trainingMapper.toCreateResponse(t));
        }
        return responses;
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ValidationException("'from' date must be less than or equal to 'to' date");
        }
    }


    private void validateTrainingRequest(TrainingCreateRequest request) {
        commonValidator.validateNotNull(request, "Add training request");

        commonValidator.validateNotNull(request.trainerId(), "Trainer ID");
        commonValidator.validateNotNull(request.trainingTypeId(), "TrainingType ID");

        commonValidator.validateNotBlank(request.trainingName(), "Training name");
        commonValidator.validateNotNull(request.trainingDate(), "Training date");
        commonValidator.validateNotNull(request.trainingDuration(), "Training duration");

        if (request.trainingDuration() <= 0) {
            throw new ValidationException("Training duration must be greater than zero");
        }
    }

    private Training buildTrainingForCreate(TrainingCreateRequest request, Trainee trainee, Trainer trainer, TrainingType trainingType) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName(request.trainingName());
        training.setTrainingDate(request.trainingDate());
        training.setTrainingDuration(request.trainingDuration());
        return training;
    }


}
