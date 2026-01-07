package com.alirizakaygusuz.gymcrm.model;

public class Trainer extends  User {

    private String specialization;
    private TrainingType trainingType;


    public Trainer() {
    }

    public Trainer(String firstName, String lastName, String username, String password, boolean isActive, String specialization, TrainingType trainingType) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
        this.trainingType = trainingType;
    }

    public Trainer(Long id, String firstName, String lastName, String username, String password, boolean isActive, String specialization, TrainingType trainingType) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
        this.trainingType = trainingType;
    }



    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }


    @Override
    public String toString() {
        return "Trainer{" +
                super.toString() +", " +
                "specialization='" + specialization + '\'' +
                ", trainingType=" + trainingType +
                '}';
    }
}
