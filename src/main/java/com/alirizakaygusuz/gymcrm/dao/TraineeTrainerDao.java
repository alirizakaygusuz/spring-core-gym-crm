package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.TraineeTrainer;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeTrainerDao {

    @PersistenceContext
    private EntityManager entityManager;


    public TraineeTrainer save(TraineeTrainer traineeTrainer) {
        entityManager.persist(traineeTrainer);
        return traineeTrainer;
    }


    public List<Trainer> findUnAssignedTrainersByTraineeUsername(String username) {
        String jpql = """
                    select tr
                    from Trainer tr
                    where tr.user.active = true
                      and not exists (
                        select 1
                        from TraineeTrainer tt
                        where tt.trainer = tr
                          and tt.trainee.user.username = :username
                      )
                """;

        return entityManager.createQuery(jpql, Trainer.class)
                .setParameter("username", username)
                .getResultList();
    }

    public void deleteAllByTraineeUsername(String traineeUsername) {
        String jpql = """
                    delete from TraineeTrainer tt
                    where tt.trainee.user.username = :username
                """;

        entityManager.createQuery(jpql)
                .setParameter("username", traineeUsername)
                .executeUpdate();
    }


}
