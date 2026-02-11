package com.alirizakaygusuz.gymcrm.config.app;

import com.alirizakaygusuz.gymcrm.config.web.WebConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan(
        basePackages = "com.alirizakaygusuz.gymcrm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = RestController.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)
        }
)
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
