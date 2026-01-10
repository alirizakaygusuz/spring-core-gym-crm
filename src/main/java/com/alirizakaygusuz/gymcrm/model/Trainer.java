package com.alirizakaygusuz.gymcrm.model;

public class Trainer extends  User {

    private TrainingType specialization;


    public Trainer() {
    }

    public Trainer(String firstName, String lastName, String username, String password, boolean isActive, TrainingType specialization) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }

    public Trainer(Long id, String firstName, String lastName, String username, String password, boolean isActive, TrainingType specialization) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }


    // Getters and Setters
    public TrainingType getSpecialization() {
        return specialization;
    }
    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }


    @Override
    public String toString() {
        return "Trainer{" +
                super.toString() +", " +
                " specialization='" + specialization +
                '}';
    }
}
