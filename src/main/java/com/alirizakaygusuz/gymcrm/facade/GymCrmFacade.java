package com.alirizakaygusuz.gymcrm.facade;

import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.service.TraineeService;
import com.alirizakaygusuz.gymcrm.service.TrainerService;
import com.alirizakaygusuz.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class GymCrmFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private static final Logger log =
            Logger.getLogger(GymCrmFacade.class.getName());


    //Injection of services via constructor
    public GymCrmFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }


    //TRAINEE METHODS
    //Create Trainee Profile
    public Trainee createTraineeProfile(Trainee trainee) {
        return traineeService.createProfile(trainee);
    }

    //Update Trainee Profile
    public Trainee updateTraineeProfile(Long id, Trainee updatedTrainee) {
        return traineeService.updateProfile(id, updatedTrainee);
    }

    //Delete Trainee Profile
    public void deleteTraineeProfile(Long id) {
        traineeService.deleteProfile(id);
    }

    //Select Trainee Profile by ID and method
    public Trainee selectTraineeProfileById(Long id) {
        return traineeService.selectProfile(id);
    }

    //Select Trainee Profile by username
    public Trainee selectTraineeProfileByUsername(String username) {
        return traineeService.selectProfile(username);
    }

    //Get All Trainee Profiles with Map
    public Map<Long,Trainee> getAllTraineeProfiles() {
        return traineeService.getAllProfiles();
    }

    //TRAINER METHODS
    //Create Trainer Profile
    public Trainer createTrainerProfile(Trainer trainer) {
        return trainerService.createProfile(trainer);
    }

    //Update Trainer Profile
    public Trainer updateTrainerProfile(Long id, Trainer updatedTrainer) {
        return trainerService.updateProfile(id, updatedTrainer);
    }

    //Select Trainer Profile by ID
    public Trainer selectTrainerProfileById(Long id) {
        return trainerService.selectProfile(id);
    }

    //Select Trainer Profile by username
    public Trainer selectTrainerProfileByUsername(String username) {
        return trainerService.selectProfile(username);
    }


    //Get All Trainer Profiles
    public Map<Long,Trainer> getAllTrainerProfiles() {
        return trainerService.getAllTrainers();
    }

    //TRAINING METHODS
    //CREATE Training Profile
    public Training createTrainingProfile(Training training) {
        //Log check Training object is not null
        if (training == null) {
            log.warning("Training object is null in createTrainingProfile method");
            throw new IllegalArgumentException("Training object cannot be null");
        }

        //Log the trainee and trainer profiles being selected during training creation
        log.info("Selecting trainee and trainer profiles for training creation. Trainee ID: " + training.getTraineeId() +
                ", Trainer ID: " + training.getTrainerId());
        traineeService.selectProfile(training.getTraineeId());
        trainerService.selectProfile(training.getTrainerId());

        return trainingService.createProfile(training);
    }

    //Select Training Profile by ID
    public Training selectTrainingProfile(Long trainingId) {
        return trainingService.selectProfile(trainingId);
    }

    //Get All Training Profiles
    public Map<Long,Training> getAllTrainingProfiles() {
        return trainingService.getAllTrainings();
    }



}