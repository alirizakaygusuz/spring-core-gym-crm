package com.alirizakaygusuz.gymcrm.workload_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "trainer_workload_yearly_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadYearlySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true ,fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_workload_yearly_summary_id")
    private List<TrainerWorkloadMonthlySummary> monthlySummaries;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainerWorkloadYearlySummary t)) return false;
        return Objects.equals(id,t.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
