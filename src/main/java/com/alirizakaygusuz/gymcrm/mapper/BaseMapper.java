package com.alirizakaygusuz.gymcrm.mapper;

public interface BaseMapper<REGISTER_SRC, E, REGISTER_RESPONSE, PROFILE_RESPONSE, UPDATE_PROFILE_RESPONSE> {


    REGISTER_RESPONSE toRegisterResponse(REGISTER_SRC source);

    PROFILE_RESPONSE toProfileResponse(E entity);

    UPDATE_PROFILE_RESPONSE toProfileUpdateResponse(E entity);
}
