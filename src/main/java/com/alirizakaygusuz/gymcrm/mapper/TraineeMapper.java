package com.alirizakaygusuz.gymcrm.mapper;

import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;
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
public interface TraineeMapper extends BaseMapper<User, Trainee, TraineeRegisterResponse, TraineeProfileResponse, TraineeProfileUpdateResponse> {



    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    TraineeRegisterResponse toRegisterResponse(User user);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", expression = "java(trainee.isActive())")
    @Mapping(target = "trainers", source = "trainerLinks", qualifiedByName = "toTrainerSummaries")
    TraineeProfileResponse toProfileResponse(Trainee trainee);

    @Named("toTrainerSummaries")
    default List<TrainerProfileSummaryResponse> toTrainerSummaries(Set<TraineeTrainer> links) {
        if (links == null || links.isEmpty()) {
            return Collections.emptyList();
        }

        return links.stream()
                .map(TraineeTrainer::getTrainer)
                .map(this::toTrainerSummaryResponse)
                .toList();
    }


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    TrainerProfileSummaryResponse toTrainerSummaryResponse(Trainer trainer);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isActive", expression = "java(trainee.isActive())")
    @Mapping(target = "trainers", source = "trainerLinks", qualifiedByName = "toTrainerSummaries")
    TraineeProfileUpdateResponse toProfileUpdateResponse(Trainee trainee);
}
