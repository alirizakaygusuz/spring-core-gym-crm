
package com.alirizakaygusuz.gymcrm.config.storage;

import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class InMemoryStorageConfig {

    @Bean(name = "trainerStorage")
    public Map<Long , Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean(name = "traineeStorage")
    public Map<Long , Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean(name = "trainingStorage")
    public  Map<Long , Training> trainingStorage() {
        return new HashMap<>();
    }
}
