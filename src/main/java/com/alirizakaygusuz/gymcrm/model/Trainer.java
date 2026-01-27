package com.alirizakaygusuz.gymcrm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "trainers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @ManyToOne(optional = false , fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @OneToMany(mappedBy = "trainer")
    private Set<TraineeTrainer> traineeLinks = new HashSet<>();


    @OneToMany(mappedBy = "trainer")
    private Set<Training> trainings = new HashSet<>();


    @Transient
    public boolean isActive() {
        return user != null && Boolean.TRUE.equals(user.isActive());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return id != null && Objects.equals(id, trainer.id);
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

}
