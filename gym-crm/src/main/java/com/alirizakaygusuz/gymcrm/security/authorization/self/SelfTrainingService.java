package com.alirizakaygusuz.gymcrm.security.authorization.self;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@accessPolicy.isSelf(authentication, #request.trainerUsername()) and hasRole('TRAINER')")
public @interface SelfTrainingService {}