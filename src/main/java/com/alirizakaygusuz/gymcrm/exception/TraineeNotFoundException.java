package com.alirizakaygusuz.gymcrm.exception;

public class TraineeNotFoundException extends GymCrmException {

    public TraineeNotFoundException(Long id) {
        super("Trainee not found with id: " + id);
    }

    public TraineeNotFoundException(String username) {
        super("Trainee not found with username: " + username);
    }
}