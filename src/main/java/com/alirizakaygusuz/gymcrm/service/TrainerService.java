package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Service responsible for managing {@link Trainer} profiles.
 *
 * <p>This class contains application-level operations for creating, retrieving,
 * updating, and deleting trainers. It coordinates validation, credential generation,
 * and persistence by delegating to {@link UserValidator}, {@link CredentialsGenerator},
 * and {@link TrainerDao}.</p>
 */
@Service
public class TrainerService {

    private final TrainerDao trainerDao;
    private CredentialsGenerator credentialsGenerator;
    private UserValidator userValidator;

    private static final Logger log =
            Logger.getLogger(TrainerService.class.getName());

    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
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
     * Creates a new trainer profile.
     *
     * <p>This method validates the input, generates unique credentials for the trainer,
     * and persists the profile using {@link TrainerDao}.</p>
     *
     * @param trainer trainer data to be created
     * @return persisted trainer profile with generated credentials
     */
    public Trainer createProfile(Trainer trainer) {

        userValidator.validateUser(trainer);

        log.info("Creating new trainer profile for: " + trainer.getFirstName() + " " + trainer.getLastName());

        String username = credentialsGenerator.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName());
        String password = credentialsGenerator.generateRandomPassword();
        trainer.setUsername(username);
        trainer.setPassword(password);
        Trainer savedTrainer = trainerDao.save(trainer);
        log.info("Trainer profile created with id: " + savedTrainer.getId() + ", username: " + savedTrainer.getUsername());

        return savedTrainer;
    }

    public Trainer selectProfile(Long id) {
        userValidator.validateId(id);

        log.info("Selecting trainer profile with id: " + id);

        return trainerDao.findById(id).orElseThrow(() -> {
            log.warning("Trainer not found with id: " + id);
            return new RuntimeException("Trainer not found with id: " + id);
        });
    }

    public Trainer selectProfile(String username) {
        userValidator.validateUsername(username);

        log.info( "Selecting trainer profile with username: " + username);

        return trainerDao.findByUsername(username).orElseThrow(() -> {
            log.warning("Trainer not found with username: " + username);
            return new RuntimeException("Trainer not found with username: " + username);
        });
    }

    public Map<Long , Trainer> getAllTrainers() {
        log.info("Retrieving all trainers");
        return trainerDao.getAll();
    }

    public Trainer updateProfile(Long id, Trainer trainer) {
        userValidator.validateId(id);
        userValidator.validateUser(trainer);

        log.info("Updating trainer profile with id: " + id);

        Trainer currentTrainer = trainerDao.findById(id).orElseThrow(() -> {
            log.warning("Trainer not found by id: " + id);
            return new RuntimeException("Trainer not found by id:" + id);
        });

        currentTrainer.setFirstName(trainer.getFirstName());
        currentTrainer.setLastName(trainer.getLastName());
        currentTrainer.setActive(trainer.isActive());
        currentTrainer.setSpecialization(trainer.getSpecialization());


        Trainer updatedTrainer = trainerDao.update(id, currentTrainer);
        log.info("Trainer profile updated with id: " + updatedTrainer.getId());
        return updatedTrainer;
    }

}