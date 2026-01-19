package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.seed.dto.TraineeSeedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TraineeSeedMapper implements SeedBaseMapper<Trainee, TraineeSeedDto> {


    private UserSeedMapperApplier userSeedMapperApplier;

    @Autowired
    public void setUserSeedMapperSupport(UserSeedMapperApplier userSeedMapperApplier) {
        this.userSeedMapperApplier = userSeedMapperApplier;
    }

    @Override
    public Trainee toEntity(TraineeSeedDto dto) {
        Trainee t = new Trainee();
        userSeedMapperApplier.apply(t, dto.id(), dto.firstName(), dto.lastName(), dto.isActive());
        t.setDateOfBirth(dto.dateOfBirth());
        t.setAddress(dto.address());

        return t;
    }
}
