package com.alirizakaygusuz.gymcrm.model;

public enum TrainingTypeCode {
    CARDIO("Cardio"),
    STRENGTH("Strength"),
    FLEXIBILITY("Flexibility"),
    BALANCE("Balance"),
    YOGA("Yoga"),
    PILATES("Pilates"),
    CROSSFIT("CrossFit"),
    FUNCTIONAL_TRAINING("Functional Training"),
    HIIT("HIIT"),
    MOBILITY("Mobility"),
    ENDURANCE("Endurance");

    private final String trainingTypeName;

    TrainingTypeCode(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

}
