package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.config.mapper.BaseMapperConfig;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.seed.dto.TraineeSeedDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(config = BaseMapperConfig.class)
public abstract class TraineeSeedMapper implements SeedBaseMapper<Trainee, TraineeSeedDto> {


    protected UserSeedMapperApplier userSeedMapperApplier;

    @Autowired
    public void setUserSeedMapperApplier(UserSeedMapperApplier userSeedMapperApplier) {
        this.userSeedMapperApplier = userSeedMapperApplier;
    }


    @Override
    @Mapping(target = "active", source = "isActive")
    public abstract Trainee toEntity(TraineeSeedDto dto);

    @AfterMapping
    protected void applyUserFields(TraineeSeedDto dto, @MappingTarget Trainee trainee) {
        userSeedMapperApplier.apply(
                trainee,
                dto.firstName(),
                dto.lastName()
        );
    }
}
