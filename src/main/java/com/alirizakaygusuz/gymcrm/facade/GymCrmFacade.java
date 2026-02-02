package com.alirizakaygusuz.gymcrm.facade;

import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeCreateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeProfileRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerCreateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.training.TraineeTrainingQueryRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainerTrainingQueryRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingResponse;
import com.alirizakaygusuz.gymcrm.service.TraineeService;
import com.alirizakaygusuz.gymcrm.service.TrainerService;
import com.alirizakaygusuz.gymcrm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Facade that exposes a simplified API for Gym CRM use-cases.
 *
 * <p>This class coordinates operations related to trainees, trainers, and trainings,
 * acting as a single entry point for application-level workflows.</p>
 */
@Component
@RequiredArgsConstructor
public class GymCrmFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;


    public TraineeCreateResponse createTraineeProfile(TraineeProfileRequest request) {
        return traineeService.createProfile(request);
    }

    public TraineeProfileResponse getTraineeProfile(LoginRequest login) {
        return traineeService.selectProfile(login);
    }

    public TraineeProfileResponse updateTraineeProfile(LoginRequest login, TraineeProfileRequest request) {
        return traineeService.updateProfile(login, request);
    }

    public void deleteTraineeProfile(LoginRequest login) {
        traineeService.deleteTrainee(login);
    }

    public void activateTrainee(LoginRequest login) {
        traineeService.activateTrainee(login);
    }

    public void deactivateTrainee(LoginRequest login) {
        traineeService.deactivateTrainee(login);
    }

    public void changeTraineePassword(LoginRequest login, String newPassword) {
        traineeService.changePassword(login, newPassword);
    }

    public List<TrainerProfileResponse> getUnassignedTrainers(LoginRequest login) {
        return traineeService.getUnassignedTrainers(login);
    }

    public void updateTraineeTrainers(LoginRequest login, List<Long> trainerIds) {
        traineeService.updateTraineeTrainers(login, trainerIds);
    }

    public TrainerCreateResponse createTrainerProfile(TrainerProfileRequest request) {
        return trainerService.createProfile(request);
    }

    public TrainerProfileResponse getTrainerProfile(LoginRequest login) {
        return trainerService.selectProfile(login);
    }

    public TrainerProfileResponse updateTrainerProfile(LoginRequest login, TrainerProfileRequest request) {
        return trainerService.updateTrainerProfile(login, request);
    }

    public void activateTrainer(LoginRequest login) {
        trainerService.activateTrainer(login);
    }

    public void deactivateTrainer(LoginRequest login) {
        trainerService.deactivateTrainer(login);
    }

    public void changeTrainerPassword(LoginRequest login, String newPassword) {
        trainerService.changePassword(login, newPassword);
    }

    public TrainingResponse addTraining(LoginRequest login, TrainingCreateRequest request) {
        return trainingService.addTraining(login, request);
    }

    public List<TrainingResponse> getTraineeTrainings(LoginRequest login, TraineeTrainingQueryRequest query) {
        return trainingService.getTraineeTrainings(login, query);
    }

    public List<TrainingResponse> getTrainerTrainings(LoginRequest login, TrainerTrainingQueryRequest query) {
        return trainingService.getTrainerTrainings(login, query);
    }

}