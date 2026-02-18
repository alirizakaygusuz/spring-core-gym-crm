package com.alirizakaygusuz.gymcrm.model;

public enum TrainingTypeCode {
    CARDIO("CARDIO"),
    STRENGTH("STRENGTH"),
    FLEXIBILITY("FLEXIBILITY"),
    BALANCE("BALANCE"),
    YOGA("YOGA"),
    PILATES("PILATES"),
    CROSSFIT("CROSSFIT"),
    FUNCTIONAL_TRAINING("FUNCTIONAL_TRAINING"),
    HIIT("HIIT"),
    MOBILITY("MOBILITY"),
    ENDURANCE("ENDURANCE");

    private final String trainingTypeName;

    TrainingTypeCode(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public static TrainingTypeCode fromString(String trainingTypeName) {
        if (trainingTypeName.isBlank()) {
            return null;
        }
        for (TrainingTypeCode type : TrainingTypeCode.values()) {
            if (type.trainingTypeName.equalsIgnoreCase(trainingTypeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with training type name: " + trainingTypeName);
    }

}