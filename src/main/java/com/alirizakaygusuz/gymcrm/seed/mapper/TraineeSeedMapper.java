package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.seed.dto.TraineeSeedDto;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TraineeSeedMapper implements SeedBaseMapper<Trainee , TraineeSeedDto> {

    private static final Logger log = Logger.getLogger(TraineeSeedMapper.class.getName());

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

        log.info(t.toString());
        return t;
    }
}
