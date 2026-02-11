package com.alirizakaygusuz.gymcrm.mapper;

import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface TrainingMapper {


    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingDate", source = "trainingDate")
    @Mapping(target = "trainingType", source = "trainingType.trainingTypeName")
    @Mapping(target = "trainingDuration", source = "trainingDuration")
    @Mapping(target = "trainerName", source = "trainer.user.username")
    TraineeTrainingFilterResponse toTraineeTrainingFilterResponse(Training training);


    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingDate", source = "trainingDate")
    @Mapping(target = "trainingType", source = "trainingType.trainingTypeName")
    @Mapping(target = "trainingDuration", source = "trainingDuration")
    @Mapping(target = "traineeName", source = "trainee.user.username")
    TrainerTrainingFilterResponse toTrainerTrainingFilterResponse(Training training);
}
