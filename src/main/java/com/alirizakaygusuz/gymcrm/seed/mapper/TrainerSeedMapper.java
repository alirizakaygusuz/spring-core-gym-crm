package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.seed.dto.TrainerSeedDto;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TrainerSeedMapper implements SeedBaseMapper<Trainer , TrainerSeedDto> {

    private static final Logger log = Logger.getLogger(TrainerSeedMapper.class.getName());

    private UserSeedMapperApplier userSeedMapperApplier;

    @Autowired
    public void setUserSeedMapperSupport(UserSeedMapperApplier userSeedMapperApplier) {
        this.userSeedMapperApplier = userSeedMapperApplier;
    }

    @Override
    public Trainer toEntity(TrainerSeedDto dto) {
        Trainer t = new Trainer();
        userSeedMapperApplier.apply(t, dto.id(), dto.firstName(), dto.lastName(), dto.isActive());
        t.setSpecialization(dto.specialization());
        log.info(t.toString());
        return t;
    }
}
