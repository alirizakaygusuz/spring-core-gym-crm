package com.alirizakaygusuz.gymcrm.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTrainerId implements Serializable {

    @Column(name = "trainee_id")
    private Long traineeId;

    @Column(name = "trainer_id")
    private Long trainerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TraineeTrainerId that)) return false;
        return Objects.equals(traineeId, that.traineeId)
                && Objects.equals(trainerId, that.trainerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traineeId, trainerId);
    }
}
