package com.alirizakaygusuz.gymcrm.mapper;

import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingResponse;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface TrainingMapper {

    @Mapping(target = "trainingId", source = "id")
    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "traineeId", source = "trainee.id")
    @Mapping(target = "trainingTypeId", source = "trainingType.id")
    TrainingResponse toCreateResponse(Training entity);


}
