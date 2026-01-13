package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.dao.util.IdSequence;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDao {

    private Map<Long, Trainee> traineeStorage;
    private IdSequence idSequence =  new IdSequence();;


    @Autowired
    public void setTraineeStorage(@Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    // Create a new trainee
    public Trainee save(Trainee trainee) {
        idSequence.syncFrom(traineeStorage);
        long id = idSequence.next();
        trainee.setId(id);
        traineeStorage.put(id, trainee);
        return trainee;
    }

    // Retrieve a trainee by ID should return Optional <Trainee> instead of null
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    // Retrieve a trainee by username should return Optional <Trainee> instead of null and check for null username
    public Optional<Trainee> findByUsername(String username) {
        if(username == null) {
            return Optional.empty();
        }
        return traineeStorage.values().stream()
                .filter(trainee -> username.equals(trainee.getUsername()))
                .findFirst();
    }


    //Retrieve all trainees
    public Map<Long, Trainee> getAll() {
        return Map.copyOf(traineeStorage);
    }

    // Update an existing trainee
    public Trainee update(Long id, Trainee trainee) {
        trainee.setId(id);
        traineeStorage.put(id, trainee);
        return trainee;
    }

    // Delete a trainee by ID
    public void delete(Long id) {
        traineeStorage.remove(id);
    }

    // Check if a trainee exists by Username
    public boolean existsByUsername(String username) {
        if(username == null) {
            return false;
        }
        return traineeStorage.values().stream()
                .anyMatch(trainee ->  username.equals(trainee.getUsername()));
    }

}
