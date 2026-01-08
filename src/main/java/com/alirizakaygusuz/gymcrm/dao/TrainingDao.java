package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDao {

    private  Map<Long, Training> trainingStorage;
    private long trainingIdSeq = 1L;


    //Set training storage via setter injection
    @Autowired
    public void setTrainingStorage(@Qualifier("trainingStorage") Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }


    // Create a new training
    public Training save(Training training) {
        long id = trainingIdSeq++;
        training.setId(id);
        trainingStorage.put(id, training);
        return training;
    }


    //Retrieve a training by ID should return Optional <Training> instead of null
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    //Retrieve all trainings
    public Map<Long, Training> getAll() {
        return Map.copyOf(trainingStorage);

    }

}
