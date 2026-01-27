package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.dao.util.IdSequence;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;


/**
 * Data Access Object for managing {@link Trainee} entities.
 *
 * <p>This class provides in-memory persistence operations for trainees,
 * including creation, retrieval (by id / username and as a full collection),
 * update, delete, and existence checks.</p>
 *
 * <p>The storage is backed by a {@link Map} and identifier values are generated
 * using {@link IdSequence}.</p>
 */
@Repository
public class TraineeDao {

    private Map<Long, Trainee> traineeStorage;
    private IdSequence idSequence =  new IdSequence();


    @Autowired
    public void setTraineeStorage(@Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    public Trainee save(Trainee trainee) {
        idSequence.syncFrom(traineeStorage);
        long id = idSequence.next();
        trainee.setId(id);
        traineeStorage.put(id, trainee);
        return trainee;
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    public Optional<Trainee> findByUsername(String username) {
        if(username == null) {
            return Optional.empty();
        }
        return traineeStorage.values().stream()
                .filter(trainee -> username.equals(trainee.getUsername()))
                .findFirst();
    }

    public Map<Long, Trainee> getAll() {
        return Map.copyOf(traineeStorage);
    }

    public Trainee update(Long id, Trainee trainee) {
        trainee.setId(id);
        traineeStorage.put(id, trainee);
        return trainee;
    }

    public void delete(Long id) {
        traineeStorage.remove(id);
    }

    public boolean existsByUsername(String username) {
        if(username == null) {
            return false;
        }
        return traineeStorage.values().stream()
                .anyMatch(trainee ->  username.equals(trainee.getUsername()));
    }

}
