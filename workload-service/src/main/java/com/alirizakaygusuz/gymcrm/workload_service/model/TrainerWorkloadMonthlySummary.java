package com.alirizakaygusuz.gymcrm.workload_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Entity
@Table(name = "trainer_workload_monthly_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadMonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workload_month")
    private Integer month;

    private Integer totalTrainingDuration;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainerWorkloadMonthlySummary t)) return false;
        return Objects.equals(id,t.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}