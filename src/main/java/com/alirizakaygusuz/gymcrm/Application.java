package com.alirizakaygusuz.gymcrm;

import com.alirizakaygusuz.gymcrm.config.app.AppConfig;
import com.alirizakaygusuz.gymcrm.config.app.DevConfig;
import com.alirizakaygusuz.gymcrm.console.ConsoleRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext()) {

            context.getEnvironment().setActiveProfiles("dev");

            context.register(AppConfig.class, DevConfig.class);
            context.refresh();

            context.getBean(ConsoleRunner.class).run();

        }
    }

}
