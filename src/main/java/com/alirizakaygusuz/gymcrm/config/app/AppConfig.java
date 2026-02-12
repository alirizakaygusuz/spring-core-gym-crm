package com.alirizakaygusuz.gymcrm.config.app;

import com.alirizakaygusuz.gymcrm.config.web.WebConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(
        basePackages = "com.alirizakaygusuz.gymcrm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.alirizakaygusuz\\.gymcrm\\.controller\\..*")
        }
)
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
