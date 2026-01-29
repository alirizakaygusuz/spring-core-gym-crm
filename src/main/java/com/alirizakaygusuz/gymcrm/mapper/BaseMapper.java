package com.alirizakaygusuz.gymcrm.mapper;

public interface BaseMapper<E ,P ,C> {
   P toProfileResponse(E entity);
   C toCreateResponse(E entity);
}
