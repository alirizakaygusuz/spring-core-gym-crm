package com.alirizakaygusuz.gymcrm.dao;

import com.alirizakaygusuz.gymcrm.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Training save(Training training) {
        entityManager.persist(training);
        return training;
    }

    public List<Training> findTraineeTrainingsByCriteria(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerName,
            TrainingTypeCode trainingType
    ) {
        TrainingQueryCtx ctx = baseQuery();

        ctx.predicates.add(
                ctx.cb.equal(ctx.traineeUser.get("username"), traineeUsername)
        );

        addDateRange(ctx, from, to);

        addNameLike(
                ctx,
                ctx.trainerUser.get("firstName"),
                ctx.trainerUser.get("lastName"),
                trainerName
        );

        if (trainingType != null) {
            Join<Training, TrainingType> type = ctx.training.join("trainingType");
            ctx.predicates.add(
                    ctx.cb.equal(type.get("trainingTypeName"), trainingType)
            );
        }

        finalizeQuery(ctx);
        return entityManager.createQuery(ctx.cq).getResultList();
    }

    public List<Training> findTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeName
    ) {
        TrainingQueryCtx ctx = baseQuery();

        ctx.predicates.add(
                ctx.cb.equal(ctx.trainerUser.get("username"), trainerUsername)
        );

        addDateRange(ctx, from, to);

        addNameLike(
                ctx,
                ctx.traineeUser.get("firstName"),
                ctx.traineeUser.get("lastName"),
                traineeName
        );

        finalizeQuery(ctx);
        return entityManager.createQuery(ctx.cq).getResultList();
    }


    private TrainingQueryCtx baseQuery() {
        TrainingQueryCtx ctx = new TrainingQueryCtx();

        ctx.cb = entityManager.getCriteriaBuilder();
        ctx.cq = ctx.cb.createQuery(Training.class);
        ctx.training = ctx.cq.from(Training.class);

        ctx.trainer = ctx.training.join("trainer");
        ctx.trainerUser = ctx.trainer.join("user");

        ctx.trainee = ctx.training.join("trainee");
        ctx.traineeUser = ctx.trainee.join("user");

        return ctx;
    }

    private void addDateRange(TrainingQueryCtx ctx, LocalDate from, LocalDate to) {
        if (from != null) {
            ctx.predicates.add(
                    ctx.cb.greaterThanOrEqualTo(ctx.training.get("trainingDate"), from)
            );
        }
        if (to != null) {
            ctx.predicates.add(
                    ctx.cb.lessThanOrEqualTo(ctx.training.get("trainingDate"), to)
            );
        }
    }

    private void addNameLike(
            TrainingQueryCtx ctx,
            Path<String> firstName,
            Path<String> lastName,
            String name
    ) {
        if (name == null || name.isBlank()) return;

        String pattern = "%" + name.toLowerCase() + "%";
        ctx.predicates.add(
                ctx.cb.or(
                        ctx.cb.like(ctx.cb.lower(firstName), pattern),
                        ctx.cb.like(ctx.cb.lower(lastName), pattern)
                )
        );
    }

    private void finalizeQuery(TrainingQueryCtx ctx) {
        ctx.cq.select(ctx.training)
                .where(ctx.cb.and(ctx.predicates.toArray(new Predicate[0])))
                .orderBy(ctx.cb.desc(ctx.training.get("trainingDate")));
    }


    private static class TrainingQueryCtx {
        CriteriaBuilder cb;
        CriteriaQuery<Training> cq;
        Root<Training> training;

        Join<Training, Trainer> trainer;
        Join<Trainer, User> trainerUser;

        Join<Training, Trainee> trainee;
        Join<Trainee, User> traineeUser;

        List<Predicate> predicates = new ArrayList<>();
    }
}