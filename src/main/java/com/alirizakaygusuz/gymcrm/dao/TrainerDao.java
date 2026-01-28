package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class TrainerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Trainer save(Trainer trainer) {
        entityManager.persist(trainer);
        return trainer;
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    public Optional<Trainer> findByUsername(String username) {
        return entityManager.createQuery(
                        "select t from Trainer t join t.user u where u.username = :username",
                        Trainer.class
                )
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }


    public Trainer update(Trainer trainer) {
        return entityManager.merge(trainer);
    }


}
