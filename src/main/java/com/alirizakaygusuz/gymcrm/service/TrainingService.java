package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class TrainingService {

    private final TrainingDao trainingDao;

    private static final Logger log =
            Logger.getLogger(TrainingService.class.getName());

    //Inject DAOs via constructor injection
    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    //Create a new training profile and method name is createProfile
    public Training createProfile(Training training) {
        validateTraining(training);
        //Create a log entry when a new training profile is being created
        log.info("Creating new training profile for: " + training.getTrainingName());

        Training savedTraining = trainingDao.save(training);

        //Create a log entry after the training profile is created
        log.info("Training profile created with id: " + savedTraining.getId() + ", training name: " + savedTraining.getTrainingName());

        return savedTraining;
    }


    //Select training profile by id
    public Training selectProfile(Long id) {
        //Create a log when selecting a training profile
        log.info("Selecting training profile with id: " + id);
        return trainingDao.findById(id).orElseThrow(() -> {
            log.warning("Training not found with id: " + id);
            return new RuntimeException("Training not found with id: " + id);
        });
    }

    //Check if training null or not if it is null throw IllegalArgumentException and log a warning
    private void validateTraining(Training training) {
        if (training == null) {
            log.warning("Training object is null");
            throw new IllegalArgumentException("Training object cannot be null");
        }
    }


}
