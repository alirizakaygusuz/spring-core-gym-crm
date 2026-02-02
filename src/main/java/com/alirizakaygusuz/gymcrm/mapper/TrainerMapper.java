package com.alirizakaygusuz.gymcrm.mapper;


import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerCreateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface TrainerMapper extends BaseMapper<Trainer, TrainerProfileResponse, TrainerCreateResponse> {


    @Override
    @Mapping(target = "trainerId", source = "id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "active", source = "user.active")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "specializationId", source = "specialization.id")
    TrainerProfileResponse toProfileResponse(Trainer trainer);

    @Override
    @Mapping(target = "trainerId", source = "id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "active", source = "user.active")
    TrainerCreateResponse toCreateResponse(Trainer trainer);
}
