package com.alirizakaygusuz.gymcrm.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TraineeTrainerId implements Serializable {

    @Column(name = "trainee_id")
    @EqualsAndHashCode.Include
    private Long traineeId;

    @Column(name = "trainer_id")
    @EqualsAndHashCode.Include
    private Long trainerId;

}
