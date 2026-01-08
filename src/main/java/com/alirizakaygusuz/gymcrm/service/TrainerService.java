package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.service.util.CredentialsGenerator;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class TrainerService {

    private final TrainerDao trainerDao;
    private final CredentialsGenerator credentialsGenerator;

    private static final Logger log =
            Logger.getLogger(TrainerService.class.getName());

    //Inject DAOs and Utilities via constructor injection
    public TrainerService(TrainerDao trainerDao, CredentialsGenerator credentialsGenerator) {
        this.trainerDao = trainerDao;
        this.credentialsGenerator = credentialsGenerator;
    }

    //Create a new trainer profile with generated credentials and method name is createProfile
    public Trainer createProfile(Trainer trainer) {
        validateTrainer(trainer);

        //Create a log entry when a new trainer profile is being created
        log.info("Creating new trainer profile for: " + trainer.getFirstName() + " " + trainer.getLastName());

        String username = credentialsGenerator.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName());
        String password = credentialsGenerator.generateRandomPassword();
        trainer.setUsername(username);
        trainer.setPassword(password);
        Trainer savedTrainer = trainerDao.save(trainer);
        //Create a log entry after the trainer profile is created
        log.info("Trainer profile created with id: " + savedTrainer.getId() + ", username: " + savedTrainer.getUsername());

        return savedTrainer;
    }

    //Select trainer profile by id and method name is selectProfile
    public Trainer selectProfile(Long id) {
        //Create a log when selecting a trainer profile
        log.info("Selecting trainer profile with id: " + id);

        return trainerDao.findById(id).orElseThrow(() -> {
            log.warning("Trainer not found with id: " + id);
            return new RuntimeException("Trainer not found with id: " + id);
        });
    }

    //Select trainer profile by username and method name is selectProfile
    public Trainer selectProfile(String username) {
        //Create a log when selecting a trainer profile by username
        log.info("Selecting trainer profile with username: " + username);
        return trainerDao.findByUsername(username).orElseThrow(() -> {
            log.warning("Trainer not found with username: " + username);
            return new RuntimeException("Trainer not found with username: " + username);
        });
    }

    //Update trainer profile and method name is updateProfile
    public Trainer updateProfile(Long id, Trainer trainer) {
        validateTrainer(trainer);

        //Create a log when updating a trainer profile
        log.info("Updating trainer profile with id: " + id);

        trainerDao.findById(id).orElseThrow(() -> {
            log.warning("Trainer not found by id: " + id);
            return new RuntimeException("Trainer not found by id:" + id);
        });

        Trainer updatedTrainer = trainerDao.update(id, trainer);
        log.info("Trainer profile updated with id: " + updatedTrainer.getId());
        return updatedTrainer;
    }

    //Check if trainer null or not if it is null throw IllegalArgumentException and log a warning
    private void validateTrainer(Trainer trainer) {
        if (trainer == null) {
            log.warning("Trainer object is null");
            throw new IllegalArgumentException("Trainer object cannot be null");
        }
    }
}