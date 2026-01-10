package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.seed.dto.TrainingSeedDto;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TrainingSeedMapper implements SeedBaseMapper<Training, TrainingSeedDto> {

    private static final Logger log = Logger.getLogger(TrainingSeedMapper.class.getName());

    @Override
    public Training toEntity(TrainingSeedDto dto) {
        Training t = new Training();

        t.setId(dto.id());
        t.setTraineeId(dto.traineeId());
        t.setTrainerId(dto.trainerId());
        t.setTrainingType(dto.trainingType());
        t.setTrainingName(dto.trainingName());
        t.setTrainingDate(dto.trainingDate());
        t.setTrainingDurationMinutes(dto.trainingDuration());
        log.info(t.toString());

        return t;
    }
}
