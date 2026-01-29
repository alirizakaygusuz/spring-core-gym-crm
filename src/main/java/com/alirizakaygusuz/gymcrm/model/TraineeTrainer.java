package com.alirizakaygusuz.gymcrm.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "trainee_trainer")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TraineeTrainer {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private TraineeTrainerId id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("traineeId")
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("trainerId")
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;
}
