package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service responsible for managing {@link Trainee} profiles.
 *
 * <p>This class contains application-level operations for creating, retrieving,
 * updating, and deleting trainees. It coordinates validation, credential generation,
 * and persistence by delegating to {@link UserValidator}, {@link CredentialsGenerator},
 * and {@link TraineeDao}.</p>
 */

@Service
@Slf4j
public class TraineeService {

    private final TraineeDao traineeDao;
    private CredentialsGenerator credentialsGenerator;
    private UserValidator userValidator;


    public TraineeService(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Autowired
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }


    /**
     * Creates a new trainee profile.
     *
     * <p>This method validates the input, generates unique credentials for the trainee,
     * and persists the profile using {@link TraineeDao}.</p>
     *
     * @param trainee trainee data to be created
     * @return persisted trainee profile with generated credentials
     */
    public Trainee createProfile(Trainee trainee) {

        userValidator.validateUser(trainee);

        log.info("Creating new trainee profile for {} {}",
                trainee.getFirstName(),
                trainee.getLastName());


        String username = credentialsGenerator.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        String password = credentialsGenerator.generateRandomPassword();
        trainee.setUsername(username);
        trainee.setPassword(password);

        Trainee savedTrainee = traineeDao.save(trainee);

        log.info("Trainee profile created with id {}, username {}",
                savedTrainee.getId(),
                savedTrainee.getUsername());

        return savedTrainee;
    }


    public Trainee selectProfile(Long id) {
        userValidator.validateId(id);

        log.info("Selecting trainee profile with id {}", id);

        return traineeDao.findById(id).orElseThrow(() -> {
            log.warn("Trainee not found with id {}", id);
            return new RuntimeException("Trainee not found with id: " + id);
        });
    }

    public Trainee selectProfile(String username) {
        userValidator.validateUsername(username);

        log.info("Selecting trainee profile with username {}", username);

        return traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainee not found with username {}", username);
            return new RuntimeException("Trainee not found with username: " + username);
        });
    }

    public Map<Long, Trainee> getAllProfiles() {
        log.info("Retrieving all trainee profiles");

        return traineeDao.getAll();
    }

    public Trainee updateProfile(Long id, Trainee trainee) {

        userValidator.validateId(id);
        userValidator.validateUser(trainee);

        log.info("Updating trainee profile with id {}", id);

        Trainee currentTrainee = traineeDao.findById(id).orElseThrow(() -> {
            log.warn("Trainee not found with id {}", id);
            return new RuntimeException("Trainee not found with id: " + id);
        });

        currentTrainee.setFirstName(trainee.getFirstName());
        currentTrainee.setLastName(trainee.getLastName());
        currentTrainee.setActive(trainee.isActive());
        currentTrainee.setDateOfBirth(trainee.getDateOfBirth());
        currentTrainee.setAddress(trainee.getAddress());

        Trainee updatedTrainee = traineeDao.update(id, currentTrainee);
        log.info("Trainee profile updated with id {}", id);

        return updatedTrainee;
    }

    public void deleteProfile(Long id) {
        userValidator.validateId(id);

        log.info("Deleting trainee profile with id {}", id);

        traineeDao.findById(id).orElseThrow(() -> {
            log.warn("Trainee not found with id {}", id);

            return new RuntimeException("Trainee not found with id: " + id);
        });

        traineeDao.delete(id);

        log.info("Deleted trainee profile with id {}", id);
    }

    public void deleteProfile(String username) {
        userValidator.validateUsername(username);

        log.info("Deleting trainee profile with username {}", username);


        Trainee trainee = traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainee not found with username {}", username);
            return new RuntimeException("Trainee not found with username: " + username);
        });

        traineeDao.delete(trainee.getId());
        log.info("Deleted trainee profile with username {}", username);

    }

}