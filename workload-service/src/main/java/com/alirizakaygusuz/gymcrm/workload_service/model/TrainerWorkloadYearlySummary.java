package com.alirizakaygusuz.gymcrm.workload_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadYearlySummary {



    private Integer year;


    private List<TrainerWorkloadMonthlySummary> monthlySummaries = new ArrayList<>();



}
