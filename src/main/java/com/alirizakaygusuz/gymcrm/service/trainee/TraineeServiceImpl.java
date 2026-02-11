package com.alirizakaygusuz.gymcrm.service.trainee;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TraineeTrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.mapper.TraineeMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainingMapper;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.service.user.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.SelfAccessValidator;
import com.alirizakaygusuz.gymcrm.service.validator.TrainingDateRangeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.alirizakaygusuz.gymcrm.model.TrainingTypeCode.fromString;


@Service
@Slf4j
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final TraineeTrainerDao traineeTrainerDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;

    private final UserService userService;

    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    private final TrainingDateRangeValidator trainingDateRangeValidator;
    private final SelfAccessValidator selfAccessValidator;


    @Override
    @Transactional
    public TraineeRegisterResponse register(TraineeRegisterRequest request) {
        log.info("Starting trainee registration ");
        User savedUser = userService.createUserWithCredentials(request);

        log.info("User profile created with username={}", savedUser.getUsername());

        Trainee trainee = buildTraineeForCreate(request, savedUser);
        Trainee savedTrainee = traineeDao.save(trainee);

        log.info("Trainee profile created. traineeId={}, username={}",
                savedTrainee.getId(), savedUser.getUsername());


        return traineeMapper.toRegisterResponse(savedTrainee.getUser());
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeProfileResponse getProfile(String currentUsername, String targetUsername) {
        log.info("Starting trainee profile selection");

        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        log.info("Selecting trainee profile with username {}", targetUsername);

        Trainee selectedTrainee = findTraineeByUsernameWithDetailsOrThrow(targetUsername);

        return traineeMapper.toProfileResponse(selectedTrainee);
    }


    @Override
    @Transactional
    public TraineeProfileUpdateResponse updateProfile(
            String currentUsername,
            String targetUsername,
            TraineeProfileUpdateRequest request
    ) {
        log.info("Starting trainee profile update. targetUsername={}", targetUsername);
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);


        Trainee trainee = findTraineeByUsernameWithDetailsOrThrow(targetUsername);

        userService.applyProfileUpdate(trainee.getUser(), request);

        applyTraineeProfile(trainee, request);

        Trainee updatedTrainee = traineeDao.update(trainee);

        log.info("Trainee profile updated. traineeId={}, username={}",
                updatedTrainee.getId(), updatedTrainee.getUser().getUsername());

        return traineeMapper.toProfileUpdateResponse(updatedTrainee);
    }

    @Override
    @Transactional
    public void deleteProfile(String currentUsername, String targetUsername) {
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        Trainee trainee = findTraineeByUsernameOrThrow(targetUsername);

        log.info("Deleting trainee profile. traineeId={}, username={}",
                trainee.getId(), trainee.getUser().getUsername());

        traineeDao.delete(trainee);


    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerProfileSummaryResponse> getNotAssignedActiveTrainers(
            String currentUsername,
            String targetUsername
    ) {

        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        List<Trainer> unassignedTrainers = traineeTrainerDao.findUnAssignedTrainersByTraineeUsername(targetUsername);

        List<TrainerProfileSummaryResponse> responses = new ArrayList<>();

        log.info("Found {} unassigned trainers for trainee with username={}",
                unassignedTrainers.size(), targetUsername);
        for (Trainer trainer : unassignedTrainers) {
            responses.add(trainerMapper.toProfileSummaryResponse(trainer));
        }

        log.info("Returning unassigned trainers for trainee with username={}",
                targetUsername);

        return responses;
    }

    @Override
    @Transactional
    public List<TrainerProfileSummaryResponse> updateTrainerList(
            String currentUsername,
            String targetUsername,
            List<String> trainerUsernames
    ) {
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        Trainee trainee = findTraineeByUsernameWithDetailsOrThrow(targetUsername);

        traineeTrainerDao.deleteAllByTraineeUsername(targetUsername);

        List<Trainer> assignedTrainers = new ArrayList<>();

        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = trainerDao.findByUsername(trainerUsername)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer", "targetUsername", trainerUsername));

            TraineeTrainer traineeTrainer = new TraineeTrainer();
            traineeTrainer.setTrainee(trainee);
            traineeTrainer.setTrainer(trainer);
            traineeTrainer.setId(new TraineeTrainerId(trainee.getId(), trainer.getId()));

            traineeTrainerDao.save(traineeTrainer);

            assignedTrainers.add(trainer);


        }


        return assignedTrainers.stream()
                .map(trainerMapper::toProfileSummaryResponse)
                .toList();
    }

    @Override
    public List<TraineeTrainingFilterResponse> getTrainings(
            String currentUsername,
            String targetUsername,
            TraineeTrainingFilterRequest filters
    ) {
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        trainingDateRangeValidator.validateDateRange(filters.periodFrom(), filters.periodTo());

        traineeDao.findByUsername(targetUsername)
                .orElseThrow(() -> new AuthenticationFailedException("Only trainee can access trainee trainings list"));


        List<Training> trainings = trainingDao.findTraineeTrainingsByCriteria(
                targetUsername,
                filters.periodFrom(),
                filters.periodTo(),
                filters.trainerName(),
                fromString(filters.trainingType())
        );


        return trainings.stream()
                .map(trainingMapper::toTraineeTrainingFilterResponse)
                .toList();
    }


    @Override
    @Transactional
    public void setActiveStatus(String currentUsername, String targetUsername, boolean isActive) {
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);
        Trainee trainee = findTraineeByUsernameOrThrow(targetUsername);
        userService.setActiveStatus(trainee.getUser(), isActive);

    }


    private Trainee findTraineeByUsernameOrThrow(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee", "username", username));
    }

    private Trainee findTraineeByUsernameWithDetailsOrThrow(String username) {
        return traineeDao.findByUsernameWithDetails(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee", "username", username));
    }


    private Trainee buildTraineeForCreate(TraineeRegisterRequest data, User user) {
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        applyTraineeProfile(trainee, data);

        return trainee;
    }


    private void applyTraineeProfile(Trainee trainee, TraineeProfileRequest data) {
        if (data.address() != null) {
            trainee.setAddress(data.address());
        }
        if (data.dateOfBirth() != null) {
            trainee.setDateOfBirth(data.dateOfBirth());
        }
    }


}