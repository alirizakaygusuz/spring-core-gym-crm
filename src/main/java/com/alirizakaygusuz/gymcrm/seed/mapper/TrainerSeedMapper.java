package com.alirizakaygusuz.gymcrm.seed.mapper;

import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.seed.dto.TrainerSeedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainerSeedMapper implements SeedBaseMapper<Trainer, TrainerSeedDto> {

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
        return t;
    }
}
