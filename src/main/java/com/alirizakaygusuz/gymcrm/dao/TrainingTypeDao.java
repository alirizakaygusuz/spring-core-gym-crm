package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.TrainingType;
import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<TrainingType> findById(Long id) {
        TrainingType trainingType = entityManager.find(TrainingType.class, id);
        return Optional.ofNullable(trainingType);
    }


    public Optional<TrainingType> findByCode(TrainingTypeCode trainingTypeName) {
        return entityManager.createQuery(
                        "select t from TrainingType t where t.trainingTypeName = :trainingTypeName",
                        TrainingType.class
                )
                .setParameter("trainingTypeName", trainingTypeName)
                .getResultStream()
                .findFirst();
    }

    public List<TrainingType> findAll() {
        return entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class)
                .getResultList();
    }
}
