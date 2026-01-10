package com.alirizakaygusuz.gymcrm.seed;

import com.alirizakaygusuz.gymcrm.seed.dto.TraineeSeedDto;
import com.alirizakaygusuz.gymcrm.seed.dto.TrainerSeedDto;
import com.alirizakaygusuz.gymcrm.seed.dto.TrainingSeedDto;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.seed.mapper.TraineeSeedMapper;
import com.alirizakaygusuz.gymcrm.seed.mapper.TrainerSeedMapper;
import com.alirizakaygusuz.gymcrm.seed.mapper.TrainingSeedMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Component
public class SeedInitializer implements BeanPostProcessor {

    private static final Logger log = Logger.getLogger(SeedInitializer.class.getName());
    private final AtomicBoolean seeded = new AtomicBoolean(false);

    private JsonSeedReader reader;
    private TraineeSeedMapper traineeSeedMapper;
    private TrainerSeedMapper trainerSeedMapper;
    private TrainingSeedMapper trainingSeedMapper;


    @Value("${storage.init.enabled:false}")
    private boolean initEnabled;

    @Value("${storage.seed.trainees:}")
    private String traineesPath;

    @Value("${storage.seed.trainers:}")
    private String trainersPath;

    @Value("${storage.seed.trainings:}")
    private String trainingsPath;

    private ObjectProvider<Map<Long, Trainee>> traineeStorageProvider;
    private ObjectProvider<Map<Long, Trainer>> trainerStorageProvider;
    private ObjectProvider<Map<Long, Training>> trainingStorageProvider;

    //Inject storages via setter injection
    @Autowired
    public void setTraineeStorageProvider(@Qualifier("traineeStorage") ObjectProvider<Map<Long, Trainee>> traineeStorageProvider) {
        this.traineeStorageProvider = traineeStorageProvider;
    }

    @Autowired
    public void setTrainerStorageProvider(@Qualifier("trainerStorage") ObjectProvider<Map<Long, Trainer>> trainerStorageProvider) {
        this.trainerStorageProvider = trainerStorageProvider;
    }

    @Autowired
    public void setTrainingStorageProvider(@Qualifier("trainingStorage") ObjectProvider<Map<Long, Training>> trainingStorageProvider) {
        this.trainingStorageProvider = trainingStorageProvider;
    }


    //Inject JsonSeedReader via setter injection
    @Autowired
    public void setJsonSeedReader(JsonSeedReader reader) {
        this.reader = reader;
    }

    //Inject mappers via setter injection
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

    private Map<Long, Trainee> traineeStorage() {
        return traineeStorageProvider.getObject();
    }

    //Trainer storage getter
    private Map<Long, Trainer> trainerStorage() {
        return trainerStorageProvider.getObject();
    }

    //Training storage getter
    private Map<Long, Training> trainingStorage() {
        return trainingStorageProvider.getObject();
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        boolean isStorageBean =
                "traineeStorage".equals(beanName)
                        || "trainerStorage".equals(beanName)
                        || "trainingStorage".equals(beanName);

        if (!isStorageBean) {
            return bean;
        }

        if (!initEnabled) {
            log.warning("Seed initialization is disabled.");
            seeded.set(true);
            return bean;
        }

        if (!seeded.compareAndSet(false, true)) return bean;

        initSeeders();
        return bean;
    }


    private void initSeeders() {
        //Log initialization start
        log.info("================Starting seed initialization=================");

        initializeTrainees();
        initializeTrainers();
        initializeTrainings();

        //Log initialization end
        log.info("================Seed initialization completed=================");
    }


    private void initializeTrainees() {
        //Create Entry Log
        log.info("---------------Starting trainee initialization-------------");

        if (!isPathValid(traineesPath, "Trainees"))
            return;

        var trainees = reader.read(traineesPath, new TypeReference<List<TraineeSeedDto>>() {
        });

        for (TraineeSeedDto t : trainees) {
            if (t == null) {
                continue;
            }

            Trainee trainee = traineeSeedMapper.toEntity(t);
            traineeStorage().put(trainee.getId(), trainee);

        }

        log.info("Initialized trainees from seed file: " + traineesPath);
    }

    private void initializeTrainers() {
        //Create Entry Log
        log.info("---------------Starting trainer initialization-------------");

        if (!isPathValid(trainersPath, "Trainers"))
            return;


        var trainersSeedDto = reader.read(trainersPath, new TypeReference<List<TrainerSeedDto>>() {
        });
        for (TrainerSeedDto t : trainersSeedDto) {
            if (t == null) {
                continue;
            }
            Trainer trainer = trainerSeedMapper.toEntity(t);
            trainerStorage().put(trainer.getId(), trainer);

        }
        log.info("Initialized trainers from seed file: " + trainersPath);
    }

    private void initializeTrainings() {
        //Create Entry Log
        log.info("---------------Starting training initialization-------------");

        if (!isPathValid(trainingsPath, "Trainings"))
            return;

        var trainingsSeedDto = reader.read(trainingsPath, new TypeReference<List<TrainingSeedDto>>() {
        });

        int skippedCount = 0;
        int addedCount = 0;
        for (TrainingSeedDto t : trainingsSeedDto) {
            if (t == null || !isValidateTrainingReferences(t.traineeId(), t.trainerId()) ) {
                skippedCount++;
                continue;
            }
            Training training = trainingSeedMapper.toEntity(t);
            trainingStorage().put(training.getId(), training);
            addedCount++;
        }

        log.info("Initialized trainings from seed file: " + trainingsPath + ". Added: " + addedCount + ", Skipped: " + skippedCount);
    }

    //Validate paths for trainees , trainers , trainings
    private boolean isPathValid(String path, String type) {
        if (path == null || path.isBlank()) {
            log.warning(type + " seed path is not provided.");
            return false;
        }
        return true;
    }


    //check traineeId and trainerId exist in their storages before adding training
    private boolean isValidateTrainingReferences(Long traineeId, Long trainerId) {
        //Check if tranieeId and trainerId are null
        if (traineeId == null || trainerId == null) {
            log.warning("Trainee ID or Trainer ID is null.");
            return false;
        }
        if (!traineeStorage().containsKey(traineeId)) {
            log.warning("Trainee ID " + traineeId + " does not exist.");
            return false;
        }
        if (!trainerStorage().containsKey(trainerId)) {
            log.warning("Trainer ID " + trainerId + " does not exist.");
            return false;
        }

        return true;
    }

}