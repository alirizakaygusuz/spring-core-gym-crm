package com.alirizakaygusuz.gymcrm.mapper;

import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeCreateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface TraineeMapper extends BaseMapper<Trainee, TraineeProfileResponse, TraineeCreateResponse> {


    @Override
    @Mapping(target = "traineeId", source = "id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "username", source = "user.username")
    TraineeProfileResponse toProfileResponse(Trainee entity);


    @Override
    @Mapping(target = "traineeId", source = "id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "isActive", source = "user.active")
    TraineeCreateResponse toCreateResponse(Trainee trainee);

}
