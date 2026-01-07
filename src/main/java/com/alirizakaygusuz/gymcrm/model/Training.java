package com.alirizakaygusuz.gymcrm.model;

import java.time.LocalDate;

public class Training {

    private Long id;
    private Long trainerId;
    private Long traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private int trainingDurationMinutes;

    public Training() {
    }

    public Training(Long trainerId, Long traineeId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDurationMinutes) {
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDurationMinutes = trainingDurationMinutes;
    }

    public Training(Long id, Long trainerId, Long traineeId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDurationMinutes) {
        this.id = id;
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDurationMinutes = trainingDurationMinutes;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDurationMinutes() {
        return trainingDurationMinutes;
    }

    public void setTrainingDurationMinutes(int trainingDurationMinutes) {
        this.trainingDurationMinutes = trainingDurationMinutes;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", trainerId=" + trainerId +
                ", traineeId=" + traineeId +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", trainingDurationMinutes=" + trainingDurationMinutes +
                '}';
    }
}
