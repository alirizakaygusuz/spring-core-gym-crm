package com.alirizakaygusuz.gymcrm;

import com.alirizakaygusuz.gymcrm.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("Hello Spring Core Gym Crm Task!");
        }
    }

}