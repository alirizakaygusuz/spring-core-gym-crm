package com.alirizakaygusuz.gymcrm.seed;

import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.seed.dto.TraineeSeedDto;
import com.alirizakaygusuz.gymcrm.seed.dto.TrainerSeedDto;
import com.alirizakaygusuz.gymcrm.seed.dto.TrainingSeedDto;
import com.alirizakaygusuz.gymcrm.seed.mapper.TraineeSeedMapper;
import com.alirizakaygusuz.gymcrm.seed.mapper.TrainerSeedMapper;
import com.alirizakaygusuz.gymcrm.seed.mapper.TrainingSeedMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;


/**
 * Initializes in-memory storage with seed data at application startup.
 *
 * <p>Seed initialization is controlled by the active Spring profile (e.g. {@code dev})
 * via {@code @Profile}, and by seed file paths configured through
 * {@code storage.seed.trainees}, {@code storage.seed.trainers}, and {@code storage.seed.trainings}.</p>
 */
@Profile("dev")
@Component
@Slf4j
public class SeedInitializer {

    private JsonSeedReader reader;
    private TraineeSeedMapper traineeSeedMapper;
    private TrainerSeedMapper trainerSeedMapper;
    private TrainingSeedMapper trainingSeedMapper;

    @Value("${storage.seed.trainees}")
    private String traineesPath;

    @Value("${storage.seed.trainers}")
    private String trainersPath;

    @Value("${storage.seed.trainings}")
    private String trainingsPath;

    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;

    @Autowired
    public void setTraineeStorage(@Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(@Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Autowired
    public void setTrainingStorage(@Qualifier("trainingStorage") Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Autowired
    public void setJsonSeedReader(JsonSeedReader reader) {
        this.reader = reader;
    }

    @Autowired
    public void setTraineeSeedMapper(TraineeSeedMapper traineeSeedMapper) {
        this.traineeSeedMapper = traineeSeedMapper;
    }

    @Autowired
    public void setTrainerSeedMapper(TrainerSeedMapper trainerSeedMapper) {
        this.trainerSeedMapper = trainerSeedMapper;
    }

    @Autowired
    public void setTrainingSeedMapper(TrainingSeedMapper trainingSeedMapper) {
        this.trainingSeedMapper = trainingSeedMapper;
    }

    @PostConstruct
    public void init() {
        log.info("================Starting seed initialization=================");

        initializeTrainees();
        initializeTrainers();
        initializeTrainings();

        log.info("================Seed initialization completed=================");
    }

    private void initializeTrainees() {
        log.info("---------------Starting trainee initialization-------------");

        if (!isPathValid(traineesPath, "Trainees")) return;

        var trainees = reader.read(traineesPath, new TypeReference<List<TraineeSeedDto>>() {
        });

        for (TraineeSeedDto t : trainees) {
            if (t == null) continue;

            Trainee trainee = traineeSeedMapper.toEntity(t);
            traineeStorage.put(trainee.getId(), trainee);
        }

        log.info("Initialized trainees from seed file {}", traineesPath);
    }

    private void initializeTrainers() {
        log.info("---------------Starting trainer initialization-------------");

        if (!isPathValid(trainersPath, "Trainers")) return;

        var trainers = reader.read(trainersPath, new TypeReference<List<TrainerSeedDto>>() {
        });

        for (TrainerSeedDto t : trainers) {
            if (t == null) continue;

            Trainer trainer = trainerSeedMapper.toEntity(t);
            trainerStorage.put(trainer.getId(), trainer);
        }

        log.info("Initialized trainers from seed file {}", trainersPath);
    }

    private void initializeTrainings() {
        log.info("---------------Starting training initialization-------------");

        if (!isPathValid(trainingsPath, "Trainings")) return;

        var trainings = reader.read(trainingsPath, new TypeReference<List<TrainingSeedDto>>() {
        });

        int skippedCount = 0;
        int addedCount = 0;

        for (TrainingSeedDto t : trainings) {
            if (t == null || !isValidateTrainingReferences(t.traineeId(), t.trainerId())) {
                skippedCount++;
                continue;
            }

            Training training = trainingSeedMapper.toEntity(t);
            trainingStorage.put(training.getId(), training);
            addedCount++;
        }

        log.info("Initialized trainings from seed file {} (added={}, skipped={})",
                trainingsPath, addedCount, skippedCount);
    }

    private boolean isPathValid(String path, String type) {
        if (path == null || path.isBlank()) {
            log.warn("{} seed path is not provided.", type);
            return false;
        }
        return true;
    }

    private boolean isValidateTrainingReferences(Long traineeId, Long trainerId) {
        if (traineeId == null || trainerId == null) {
            log.warn("Trainee ID or Trainer ID is null.");
            return false;
        }
        if (!traineeStorage.containsKey(traineeId)) {
            log.warn("Trainee ID={} does not exist.", traineeId);
            return false;
        }
        if (!trainerStorage.containsKey(trainerId)) {
            log.warn("Trainer ID={} does not exist.", trainerId);
            return false;
        }
        return true;
    }
}