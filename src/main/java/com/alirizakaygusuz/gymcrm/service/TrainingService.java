package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.exception.TrainingNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Service responsible for managing {@link Training} profiles.
 *
 * <p>This class contains application-level operations for creating, retrieving.
 * It coordinates validation and persistence by delegating to {@link CommonValidator} and {@link TrainingDao}.</p>
 */
@Service
@Slf4j
public class TrainingService {

    private final TrainingDao trainingDao;

    private CommonValidator commonValidator;

    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setCommonValidator(CommonValidator commonValidator) {
        this.commonValidator = commonValidator;
    }

    public Training createProfile(Training training) {
        validateTraining(training);
        log.info("Creating new training profile for {}", training.getTrainingName());

        Training savedTraining = trainingDao.save(training);

        log.info("Training profile created with id {}, training name {}", savedTraining.getId(), savedTraining.getTrainingName());

        return savedTraining;
    }

    public Training selectProfile(Long id) {
        commonValidator.validateId(id);

        log.info("Selecting training profile with id {}", id);


        return trainingDao.findById(id).orElseThrow(() -> {
            log.warn("Training not found with id {}" , id);
            return new TrainingNotFoundException(id);
        });
    }

    public Map<Long, Training> getAllTrainings() {
        log.info("Retrieving all training profiles");
        return trainingDao.getAll();
    }

    private void validateTraining(Training training) {
        if (training == null) {
            throw new ValidationException("Training object cannot be null");
        }
        commonValidator.validateNotBlank(training.getTrainingName(), "Training name");
    }


}
