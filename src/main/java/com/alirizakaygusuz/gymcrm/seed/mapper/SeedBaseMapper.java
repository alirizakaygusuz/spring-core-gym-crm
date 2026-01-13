package com.alirizakaygusuz.gymcrm.seed.mapper;

public interface SeedBaseMapper<E ,D>{
    E toEntity(D dto);
}
