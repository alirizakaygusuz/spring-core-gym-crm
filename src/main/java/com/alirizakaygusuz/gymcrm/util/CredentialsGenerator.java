package com.alirizakaygusuz.gymcrm.util;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Generates unique usernames and random passwords for users.
 *
 * <p>Usernames are generated in the form {@code firstName.lastName} and
 * suffixed with an incrementing number when a collision is detected.</p>
 */

@Component
public class CredentialsGenerator {


    private static final int PASSWORD_LENGTH = 10;

    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }


    /**
     * Generates a unique username based on the given first and last name.
     *
     * <p>The base username is {@code firstName.lastName}. If the username already exists,
     * an incrementing numeric suffix is appended until a unique value is found.</p>
     *
     * @param firstName user's first name
     * @param lastName user's last name
     * @return unique username
     */
    public String generateUniqueUsername(String firstName, String lastName) {
        String base = firstName + "." + lastName;
        String username = base;
        int suffix = 1;
        while (traineeDao.findByUsername(username).isPresent() || trainerDao.findByUsername(username).isPresent()) {
            username = base + suffix;
            suffix++;
        }
        return username;
    }

    /**
     * Generates a random password of fixed length using alphanumeric characters.
     *
     * @return randomly generated password
     */
    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (Math.random() * PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(index));
        }
        return password.toString();
    }
}
