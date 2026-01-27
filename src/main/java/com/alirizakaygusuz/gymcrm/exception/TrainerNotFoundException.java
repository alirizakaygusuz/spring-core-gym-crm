package com.alirizakaygusuz.gymcrm.exception;

public class TrainerNotFoundException extends GymCrmException {

    public TrainerNotFoundException(Long id) {
        super("Trainer not found with id: " + id);
    }

    public TrainerNotFoundException(String username) {
        super("Trainer not found with username: " + username);
    }
}
