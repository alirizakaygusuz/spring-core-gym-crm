package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class TraineeDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Trainee save(Trainee trainee) {
        entityManager.persist(trainee);
        return trainee;
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    public Optional<Trainee> findByUsername(String username) {
        return entityManager.createQuery(
                        "select t from Trainee t join t.user u where u.username = :username",
                        Trainee.class
                )
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public Trainee update(Trainee trainee) {
        return entityManager.merge(trainee);
    }

    public void delete(Trainee trainee) {
        entityManager.remove(trainee);
    }
}
