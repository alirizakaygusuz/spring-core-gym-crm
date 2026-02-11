package com.alirizakaygusuz.gymcrm.service.trainee;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TraineeTrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.mapper.TraineeMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.service.user.UserServiceImpl;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TraineeServiceImpl {

    private final TraineeDao traineeDao;
    private final TraineeTrainerDao traineeTrainerDao;
    private final TrainerDao trainerDao;

    private final UserServiceImpl userServiceImpl;

    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;

    private final CommonValidator commonValidator;


    @Transactional
    public TraineeCreateResponse createProfile(TraineeProfileRequest request) {
        log.info("Starting trainee profile creation");

        User savedUser = userServiceImpl.createUserWithCredentials(request);

        log.info("User profile created with username={}", savedUser.getUsername());

        Trainee trainee = buildTraineeForCreate(request, savedUser);
        Trainee savedTrainee = traineeDao.save(trainee);

        log.info("Trainee profile created. traineeId={}, username={}",
                savedTrainee.getId(), savedUser.getUsername());

        return traineeMapper.toCreateResponse(savedTrainee);
    }


    @Transactional(readOnly = true)
    public TraineeProfileResponse selectProfile(LoginRequest request) {
        log.info("Starting trainee profile selection");

        User authUser = userServiceImpl.authenticate(request);

        log.info("Selecting trainee profile with username {}", authUser.getUsername());

        Trainee selectedTrainee = findTraineeByUsernameOrThrow(authUser.getUsername());

        return traineeMapper.toProfileResponse(selectedTrainee);
    }


    @Transactional
    public TraineeProfileResponse updateProfile(
            LoginRequest request,
            TraineeProfileRequest updateRequest
    ) {

        User authUser = userServiceImpl.authenticate(request);

        log.info("Updating trainee profile. targetUsername={}", authUser.getUsername());

        Trainee trainee = findTraineeByUsernameOrThrow(authUser.getUsername());

        userServiceImpl.applyProfileUpdate(trainee.getUser(), updateRequest);

        applyTraineeProfile(trainee, updateRequest);

        Trainee updatedTrainee = traineeDao.update(trainee);

        log.info("Trainee profile updated. traineeId={}, username={}",
                updatedTrainee.getId(), updatedTrainee.getUser().getUsername());

        return traineeMapper.toProfileResponse(updatedTrainee);
    }


    @Transactional
    public void changePassword(
            LoginRequest request,
            String newPassword
    ) {
        User authUser = authenticateAndValidateTrainee(request);
        log.info("Starting password change for trainee with username={}", authUser.getUsername());


        userServiceImpl.changePassword(authUser, newPassword);
        log.info("Password changed for trainee with username={}", authUser.getUsername());
    }


    @Transactional
    public void activateTrainee(LoginRequest request) {
        User authUser = authenticateAndValidateTrainee(request);
        userServiceImpl.activate(authUser);
    }

    @Transactional
    public void deactivateTrainee(LoginRequest request) {
        User authUser = authenticateAndValidateTrainee(request);
        userServiceImpl.deactivate(authUser);
    }


    @Transactional
    public void deleteTrainee(LoginRequest request){
        User authUser = authenticateAndValidateTrainee(request);

        Trainee trainee = findTraineeByUsernameOrThrow(authUser.getUsername());
        log.info("Deleting trainee profile. traineeId={}, username={}",
                trainee.getId(), authUser.getUsername());

        traineeDao.delete(trainee);
    }


    @Transactional(readOnly = true)
    public List<TrainerProfileResponse> getUnassignedTrainers(LoginRequest request) {
        User authUser = authenticateAndValidateTrainee(request);

        List<Trainer> unassignedTrainers = traineeTrainerDao.findUnAssignedTrainersByTraineeUsername(authUser.getUsername());

        List<TrainerProfileResponse> responses = new ArrayList<>();

        log.info("Found {} unassigned trainers for trainee with username={}",
                unassignedTrainers.size(), authUser.getUsername());
        for(Trainer trainer : unassignedTrainers) {
            responses.add(trainerMapper.toProfileResponse(trainer));
        }

        log.info("Returning unassigned trainers for trainee with username={}",
                authUser.getUsername());
        return responses;
    }


    @Transactional
    public void updateTraineeTrainers(
            LoginRequest request,
            List<Long> newTrainerIds
    ) {

        User authUser = authenticateAndValidateTrainee(request);

        commonValidator.validateNotNull(newTrainerIds, "New trainer IDs list cannot be null");
        for(Long trainerId : newTrainerIds) {
            commonValidator.validateId(trainerId);
        }

        Trainee trainee = findTraineeByUsernameOrThrow(authUser.getUsername());

        traineeTrainerDao.deleteAllByTraineeUsername(authUser.getUsername());

        for(Long trainerId : newTrainerIds) {
            Trainer trainer = trainerDao.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", trainerId));

            TraineeTrainer traineeTrainer = new TraineeTrainer();
            traineeTrainer.setTrainee(trainee);
            traineeTrainer.setTrainer(trainer);
            traineeTrainer.setId(new TraineeTrainerId(trainee.getId(), trainer.getId()));

            traineeTrainerDao.save(traineeTrainer);


        }
    }

    private Trainee findTraineeByUsernameOrThrow(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee", "username", username));
    }

    private User authenticateAndValidateTrainee(LoginRequest request) {
        User authUser = userServiceImpl.authenticate(request);
        findTraineeByUsernameOrThrow(authUser.getUsername());
        return authUser;
    }

    private Trainee buildTraineeForCreate(TraineeProfileRequest data, User user) {
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