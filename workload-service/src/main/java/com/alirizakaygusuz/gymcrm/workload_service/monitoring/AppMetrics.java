package com.alirizakaygusuz.gymcrm.workload_service.monitoring;


import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppMetrics {

    private final MeterRegistry meterRegistry;


    public void incrementTrainerWorkloadAdd(){
        meterRegistry.counter("trainer.workload.add.total").increment();
    }

    public void incrementTrainerWorkloadAddAttempts(){
        meterRegistry.counter("trainer.workload.add.attempts.total").increment();
    }

    public void incrementTrainerWorkloadDelete(){
        meterRegistry.counter("trainer.workload.delete.total").increment();
    }

    public void incrementTrainerWorkloadDeleteAttempts(){
        meterRegistry.counter("trainer.workload.delete.attempts.total").increment();
    }

    public void incrementTrainerWorkloadGet(){
        meterRegistry.counter("trainer.workload.get.summary.total").increment();
    }

    public void incrementTrainerWorkloadGetAttempts(){
        meterRegistry.counter("trainer.workload.get.summary.attempts.total").increment();
    }


}


