package com.alirizakaygusuz.gymcrm;

import com.alirizakaygusuz.gymcrm.config.AppConfig;
import com.alirizakaygusuz.gymcrm.console.ConsoleRunner;
import com.alirizakaygusuz.gymcrm.facade.GymCrmFacade;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.model.TrainingType;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Application {

    public static void main(String[] args) {
        try (var context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

           context.getBean(ConsoleRunner.class).run();

        }
    }


}
