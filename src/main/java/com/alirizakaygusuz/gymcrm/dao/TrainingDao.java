package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.dao.util.IdSequence;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object for managing {@link Training} entities.
 *
 * <p>This class provides in-memory persistence operations for training objects,
 * including creation, retrieval (by id  and as a full collection),
 *
 * <p>The storage is backed by a {@link Map} and identifier values are generated
 * using {@link IdSequence}.</p>
 */

@Repository
public class TrainingDao {

    private Map<Long, Training> trainingStorage;

    private IdSequence idSequence = new IdSequence();

    @Autowired
    public void setTrainingStorage(@Qualifier("trainingStorage") Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    public Training save(Training training) {
        idSequence.syncFrom(trainingStorage);
        long id = idSequence.next();
        training.setId(id);
        trainingStorage.put(id, training);

        return training;
    }

    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    public Map<Long, Training> getAll() {
        return Map.copyOf(trainingStorage);

    }

}
