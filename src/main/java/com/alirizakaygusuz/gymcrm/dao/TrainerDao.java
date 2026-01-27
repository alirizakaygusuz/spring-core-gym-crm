package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.dao.util.IdSequence;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object for managing {@link Trainer} entities.
 *
 * <p>This class provides in-memory persistence operations for trainers,
 * including creation, retrieval (by id / username and as a full collection),
 * update, and existence checks.</p>
 *
 * <p>The storage is backed by a {@link Map} and identifier values are generated
 * using {@link IdSequence}.</p>
 */

@Repository
public class TrainerDao {

    private Map<Long, Trainer> trainerStorage;
    private IdSequence idSequence =  new IdSequence();;

    @Autowired
    public void setTrainerStorage(@Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    public Trainer save(Trainer trainer) {
        idSequence.syncFrom(trainerStorage);
        long id = idSequence.next();

        trainer.setId(id);
        trainerStorage.put(id, trainer);
        return trainer;
    }


    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    public Optional<Trainer> findByUsername(String username) {
        if(username == null) {
            return Optional.empty();
        }
        return trainerStorage.values().stream()
                .filter(trainer -> username.equals(trainer.getUsername()))
                .findFirst();
    }


    public Map<Long, Trainer> getAll() {
        return Map.copyOf(trainerStorage);
    }

    public Trainer update(Long id, Trainer trainer) {
        trainer.setId(id);
        trainerStorage.put(id, trainer);
        return trainer;
    }

    public boolean existsByUsername(String username) {
        if (username == null) {
            return false;
        }
        return trainerStorage.values().stream()
                .anyMatch(trainer -> username.equals(trainer.getUsername()));
    }
}
