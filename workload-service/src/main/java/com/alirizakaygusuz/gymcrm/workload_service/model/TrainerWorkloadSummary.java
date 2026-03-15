package com.alirizakaygusuz.gymcrm.workload_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "trainer_workload_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true ,fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_workload_summary_id")
    private List<TrainerWorkloadYearlySummary> yearlySummaries;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainerWorkloadSummary t)) return false;
        return Objects.equals(id,t.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
