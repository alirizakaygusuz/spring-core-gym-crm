package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.config.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.seed.dto.TrainingSeedDto;
import org.mapstruct.Mapper;


@Mapper(config = BaseMapperConfig.class)
public abstract class TrainingSeedMapper implements SeedBaseMapper<Training, TrainingSeedDto> {

    @Override
    public abstract Training toEntity(TrainingSeedDto dto);
}