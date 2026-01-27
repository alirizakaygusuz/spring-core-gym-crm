package com.alirizakaygusuz.gymcrm.facade;

import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.service.TraineeService;
import com.alirizakaygusuz.gymcrm.service.TrainerService;
import com.alirizakaygusuz.gymcrm.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Facade that exposes a simplified API for Gym CRM use-cases.
 *
 * <p>This class coordinates operations related to trainees, trainers, and trainings,
 * acting as a single entry point for application-level workflows.</p>
 *
 * <p>It delegates business operations to the corresponding service components and
 * orchestrates cross-domain interactions when required.</p>
 */
@Component
@Slf4j
public class GymCrmFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;


    public GymCrmFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }


    //TRAINEE METHODS
    public Trainee createTraineeProfile(Trainee trainee) {
        return traineeService.createProfile(trainee);
    }

    public Trainee updateTraineeProfile(Long id, Trainee updatedTrainee) {
        return traineeService.updateProfile(id, updatedTrainee);
    }

    public void deleteTraineeProfile(Long id) {
        traineeService.deleteProfile(id);
    }

    public Trainee selectTraineeProfileById(Long id) {
        return traineeService.selectProfile(id);
    }

    public Trainee selectTraineeProfileByUsername(String username) {
        return traineeService.selectProfile(username);
    }

    public Map<Long, Trainee> getAllTraineeProfiles() {
        return traineeService.getAllProfiles();
    }

    //TRAINER METHODS
    public Trainer createTrainerProfile(Trainer trainer) {
        return trainerService.createProfile(trainer);
    }

    public Trainer updateTrainerProfile(Long id, Trainer updatedTrainer) {
        return trainerService.updateProfile(id, updatedTrainer);
    }

    public Trainer selectTrainerProfileById(Long id) {
        return trainerService.selectProfile(id);
    }

    public Trainer selectTrainerProfileByUsername(String username) {
        return trainerService.selectProfile(username);
    }


    public Map<Long, Trainer> getAllTrainerProfiles() {
        return trainerService.getAllTrainers();
    }


    /**
     * Creates a new training session.
     *
     * <p>This method validates the input and ensures that referenced trainee and trainer
     * profiles exist before delegating the creation to {@link TrainingService}.</p>
     *
     * <p>It acts as an orchestration point for cross-domain interactions between
     * trainee, trainer, and training services.</p>
     *
     * @param training training data to be created
     * @return created training instance
     * @throws ValidationException if {@code training} is null
     */
    public Training createTrainingProfile(Training training) {
        if (training == null) {
            log.warn("Training object is null in createTrainingProfile method");
            throw new ValidationException("Training object cannot be null");
        }

        log.info("Selecting trainee and trainer profiles for training creatin: traineeId={}, trainerId={}  ", training.getTraineeId(), training.getTrainerId());
        traineeService.selectProfile(training.getTraineeId());
        trainerService.selectProfile(training.getTrainerId());

        return trainingService.createProfile(training);
    }

    public Training selectTrainingProfile(Long trainingId) {
        return trainingService.selectProfile(trainingId);
    }

    public Map<Long, Training> getAllTrainingProfiles() {
        return trainingService.getAllTrainings();
    }


}