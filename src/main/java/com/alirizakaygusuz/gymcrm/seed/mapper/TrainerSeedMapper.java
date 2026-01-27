package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.seed.dto.TrainerSeedDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = BaseMapperConfig.class)
public abstract class TrainerSeedMapper implements SeedBaseMapper<Trainer, TrainerSeedDto> {

    protected UserSeedMapperApplier userSeedMapperApplier;

    @Autowired
    public void setUserSeedMapperApplier(UserSeedMapperApplier userSeedMapperApplier) {
        this.userSeedMapperApplier = userSeedMapperApplier;
    }

    @Override
    @Mapping(target = "active", source = "isActive")
    public abstract Trainer toEntity(TrainerSeedDto dto);

    @AfterMapping
    protected void applyUserFields(TrainerSeedDto dto, @MappingTarget Trainer trainer) {
        userSeedMapperApplier.apply(
                trainer,
                dto.firstName(),
                dto.lastName()
        );
    }
}
