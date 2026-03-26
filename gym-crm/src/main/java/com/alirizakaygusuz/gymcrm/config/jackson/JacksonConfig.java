package com.alirizakaygusuz.gymcrm.config.jackson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }
}