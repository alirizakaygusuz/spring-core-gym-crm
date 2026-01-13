package com.alirizakaygusuz.gymcrm.util;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CredentialsGenerator {


    private static final int PASSWORD_LENGTH = 10;

    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    //Inject TraineeDao via setter injection
    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    //Inject TrainerDao via setter injection
    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }


    //Generate a Unique Username
    public String generateUniqueUsername(String firstName, String lastName) {
        String base = firstName + "." + lastName;
        String username = base;
        int suffix = 1;
        while (traineeDao.existsByUsername(username) || trainerDao.existsByUsername(username)) {
            username = base + suffix;
            suffix++;
        }
        return username;
    }

    // Generates a random password with a fixed length of 10 characters
    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (Math.random() * PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(index));
        }
        return password.toString();
    }
}
