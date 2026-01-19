package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.seed.dto.TrainingSeedDto;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.springframework.stereotype.Component;


@Component
public class TrainingSeedMapper implements SeedBaseMapper<Training, TrainingSeedDto> {


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

        return t;
    }
}
