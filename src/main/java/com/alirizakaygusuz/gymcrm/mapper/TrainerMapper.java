package com.alirizakaygusuz.gymcrm.mapper;


import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.TraineeTrainer;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(config = BaseMapperConfig.class)
public interface TrainerMapper extends BaseMapper<User, Trainer, TrainerRegisterResponse, TrainerProfileResponse, TrainerProfileUpdateResponse> {


    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    TrainerRegisterResponse toRegisterResponse(User user);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", expression = "java(trainer.isActive())")
    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    @Mapping(target = "trainees", source = "traineeLinks", qualifiedByName = "toTraineeSummaries")
    TrainerProfileResponse toProfileResponse(Trainer trainer);

    @Named("toTraineeSummaries")
    default List<TraineeProfileSummaryResponse> toTraineeSummaries(Set<TraineeTrainer> links) {
        if (links == null || links.isEmpty()) return Collections.emptyList();

        return links.stream()
                .map(TraineeTrainer::getTrainee)
                .map(this::toTraineeSummaryResponse)
                .toList();
    }


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    TraineeProfileSummaryResponse toTraineeSummaryResponse(Trainee trainee);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    TrainerProfileSummaryResponse toProfileSummaryResponse(Trainer trainer);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    @Mapping(target = "isActive", expression = "java(trainer.isActive())")
    @Mapping(target = "trainees", source = "traineeLinks", qualifiedByName = "toTraineeSummaries")
    TrainerProfileUpdateResponse toProfileUpdateResponse(Trainer trainer);
}
