package com.alirizakaygusuz.gymcrm.security.authorization.self;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@PreAuthorize("(hasRole('TRAINEE') and @accessPolicy.isSelf(authentication, #username))")
public @interface SelfTraineeService { }