package com.alirizakaygusuz.gymcrm.config.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("docker")
@PropertySource("classpath:application-docker.properties")
public class DockerConfig {
}
