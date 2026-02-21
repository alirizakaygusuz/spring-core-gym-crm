package com.alirizakaygusuz.gymcrm.monitoring.health;

import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Slf4j
@Component("trainingTypesHealth")
@RequiredArgsConstructor
public class TrainingTypesHealthIndicator implements HealthIndicator {

    private static final int EXPECTED_TRAINING_TYPES = 11;
    private final TrainingTypeDao trainingTypeDao;


    @Override
    public Health health() {

        var trainingTypes = trainingTypeDao.findAll();
        int actualCount = trainingTypes.size();

        try {
            if (actualCount == EXPECTED_TRAINING_TYPES) {
                return Health.up()
                        .withDetail("service", "Training Types")
                        .withDetail("status", "All training types are present")
                        .withDetail("expectedCount", EXPECTED_TRAINING_TYPES)
                        .withDetail("actualCount", actualCount)
                        .build();
            } else if (actualCount < EXPECTED_TRAINING_TYPES) {
                return Health.down()
                        .withDetail("service", "Training Types")
                        .withDetail("status", "Some training types are missing")
                        .withDetail("expectedCount", EXPECTED_TRAINING_TYPES)
                        .withDetail("actualCount", actualCount)
                        .build();
            } else {
                return Health.down()
                        .withDetail("service", "Training Types")
                        .withDetail("status", "Unexpected number of training types")
                        .withDetail("expectedCount", EXPECTED_TRAINING_TYPES)
                        .withDetail("actualCount", actualCount)
                        .build();
            }
        }catch (Exception e){
            log.error("Training Types health check failed", e);
            return Health.down()
                    .withDetail("service", "Training Types")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
