package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class TrainerService {

    private final TrainerDao trainerDao;
    private CredentialsGenerator credentialsGenerator;
    private UserValidator userValidator;

    private static final Logger log =
            Logger.getLogger(TrainerService.class.getName());

    //Inject DAOs  via constructor injection
    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    //Inject CredentialsGenerator via setter injection
    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    //Inject UserValidator via setter injection
    @Autowired
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    //Create a new trainer profile with generated credentials and method name is createProfile
    public Trainer createProfile(Trainer trainer) {

        userValidator.validateUser(trainer);

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
        userValidator.validateId(id);

        //Create a log when selecting a trainer profile
        log.info("Selecting trainer profile with id: " + id);

        return trainerDao.findById(id).orElseThrow(() -> {
            log.warning("Trainer not found with id: " + id);
            return new RuntimeException("Trainer not found with id: " + id);
        });
    }

    //Select trainer profile by username and method name is selectProfile
    public Trainer selectProfile(String username) {
        userValidator.validateUsername(username);

        //Create a log when selecting a trainer profile by username
        log.info( "Selecting trainer profile with username: " + username);

        return trainerDao.findByUsername(username).orElseThrow(() -> {
            log.warning("Trainer not found with username: " + username);
            return new RuntimeException("Trainer not found with username: " + username);
        });
    }

    //Update trainer profile and method name is updateProfile
    public Trainer updateProfile(Long id, Trainer trainer) {
        userValidator.validateId(id);
        userValidator.validateUser(trainer);

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

}