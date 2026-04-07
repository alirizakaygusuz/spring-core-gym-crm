package com.alirizakaygusuz.gymcrm.workload_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Document(collection = "trainer_workload_summary")
@CompoundIndex(name = "username_idx", def = "{'firstName': 1 , 'lastName':1}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadSummary {

    @Id
    private String id;

    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;


    private List<TrainerWorkloadYearlySummary> yearlySummaries = new ArrayList<>();


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
