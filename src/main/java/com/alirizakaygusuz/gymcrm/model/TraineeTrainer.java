package com.alirizakaygusuz.gymcrm.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "trainee_trainer")
@Getter
@Setter
@NoArgsConstructor
public class TraineeTrainer {

    @EmbeddedId
    private TraineeTrainerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("traineeId")
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("trainerId")
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TraineeTrainer that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
