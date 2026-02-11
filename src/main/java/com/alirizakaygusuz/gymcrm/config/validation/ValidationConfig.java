package com.alirizakaygusuz.gymcrm.config.validation;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        var bean = new LocalValidatorFactoryBean();
        bean.setMessageInterpolator(new ParameterMessageInterpolator());
        return bean;
    }
}