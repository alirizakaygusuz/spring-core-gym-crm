package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.service.util.CredentialsGenerator;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service

public class TraineeService {

    private final TraineeDao traineeDao;
    private final CredentialsGenerator credentialsGenerator;

    private static final Logger log =
            Logger.getLogger(TraineeService.class.getName());


    //Inject DAOs and Utilities via constructor injection
    public TraineeService(TraineeDao traineeDao, CredentialsGenerator credentialsGenerator) {
        this.traineeDao = traineeDao;
        this.credentialsGenerator = credentialsGenerator;
    }

    //Create a new trainee profile with generated credentials and method name is createProfile
    public Trainee createProfile(Trainee trainee) {
        validateTrainee(trainee);
        //Create a log entry when a new trainee profile is being created
        log.info("Creating new trainee profile for: " + trainee.getFirstName() + " " + trainee.getLastName());

        String username = credentialsGenerator.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        String password = credentialsGenerator.generateRandomPassword();
        trainee.setUsername(username);
        trainee.setPassword(password);

        Trainee savedTrainee = traineeDao.save(trainee);
        //Create a log entry after the trainee profile is created
        log.info("Trainee profile created with id: " + savedTrainee.getId() + ", username: " + savedTrainee.getUsername());

        return savedTrainee;
    }


    //Select trainee profile by id and method name is selectProfile
    public Trainee selectProfile(Long id) {

        //Create a log when selecting a trainee profile
        log.info("Selecting trainee profile with id: " + id);

        return traineeDao.findById(id).orElseThrow(() -> {
            log.warning("Trainee not found with id: " + id);
            return new RuntimeException("Trainee not found with id: " + id);
        });
    }

    //Select trainee profile by username and method name is findProfile
    public Trainee selectProfile(String username) {
        //Create a log when selecting a trainee profile by username
        log.info("Selecting trainee profile with username: " + username);

        return traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warning("Trainee not found with username: " + username);
            return new RuntimeException("Trainee not found with username: " + username);
        });
    }


    //Update trainee profile and method name is updateProfile
    public Trainee updateProfile(Long id, Trainee trainee) {
        validateTrainee(trainee);
        //Create a log when updating a trainee profile
        log.info("Updating trainee profile with id: " + id);
        traineeDao.findById(id).orElseThrow(() -> {
            log.warning("Trainee not found with id: " + id);
            return new RuntimeException("Trainee not found with id: " + id);
        });

        Trainee updatedTrainee = traineeDao.update(id, trainee);
        //Create a log after the trainee profile is updated
        log.info("Trainee profile updated with id: " + id + ", new trainee data: " + updatedTrainee.toString());

        return updatedTrainee;
    }

    //Delete trainee profile by id and method name is deleteProfile
    public void deleteProfile(Long id) {
        //Create a log when deleting a trainee profile
        log.info("Deleting trainee profile with id: " + id);

        traineeDao.findById(id).orElseThrow(() -> {
            log.warning("Trainee not found with id: " + id);

            return new RuntimeException("Trainee not found with id: " + id);
        });

        traineeDao.delete(id);

        log.info(() -> "Deleted trainee profile with id: " + id);
    }

    //Delete trainee profile by username and method name is deleteProfile
    public void deleteProfile(String username) {
        //Create a log when deleting a trainee profile by username
        log.info("Deleting trainee profile with username: " + username);

        Trainee trainee = traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warning("Trainee not found with username: " + username);
            return new RuntimeException("Trainee not found with username: " + username);
        });

        traineeDao.delete(trainee.getId());
        log.info(() -> "Deleted trainee profile with username: " + username);

    }


    //Check if trainee null or not if it is null throw IllegalArgumentException and log a warning
    private void validateTrainee(Trainee trainee) {
        if (trainee == null) {
            log.warning("Trainee object is null");
            throw new IllegalArgumentException("Trainee object cannot be null");
        }
    }

}