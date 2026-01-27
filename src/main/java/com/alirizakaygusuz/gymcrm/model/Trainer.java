package com.alirizakaygusuz.gymcrm.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "trainers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

}
