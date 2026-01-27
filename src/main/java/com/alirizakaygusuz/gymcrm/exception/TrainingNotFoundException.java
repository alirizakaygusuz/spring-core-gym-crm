package com.alirizakaygusuz.gymcrm.exception;

public class TrainingNotFoundException extends GymCrmException {
    public TrainingNotFoundException(Long id) {
        super("Training not found with id: " + id);
    }
}
