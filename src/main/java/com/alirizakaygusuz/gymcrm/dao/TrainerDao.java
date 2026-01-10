package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDao {

    private Map<Long, Trainer> trainerStorage;
    private long trainerIdSeq = 1L;


    //Set trainer storage via setter injection
    @Autowired
    public void setTrainerStorage(@Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }


    // Create a new trainer
    public Trainer createTrainer(Trainer trainer) {
        long id = trainerIdSeq++;
        trainer.setId(id);
        trainerStorage.put(id, trainer);
        return trainer;
    }

    // Retrieve a trainer by ID should return Optional <Trainer> instead of null
    public Optional<Trainer> findTrainerById(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    //Retrieve all trainers
    public Map<Long, Trainer> getAllTrainers() {
        return Map.copyOf(trainerStorage);
    }

    // Update an existing trainer
    public Trainer updateTrainer(Long id, Trainer trainer) {
        trainer.setId(id);
        trainerStorage.put(id, trainer);
        return trainer;
    }

    // Check if a trainer exists by Username
    public boolean existsByUsername(String username) {
        if (username == null) {
            return false;
        }
        return trainerStorage.values().stream()
                .anyMatch(trainer -> username.equals(trainer.getUsername()));
    }
}
