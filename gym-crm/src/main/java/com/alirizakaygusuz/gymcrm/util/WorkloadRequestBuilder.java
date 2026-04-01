package com.alirizakaygusuz.gymcrm.util;

import com.alirizakaygusuz.gymcrm.messaging.dto.ActionType;
import com.alirizakaygusuz.gymcrm.messaging.dto.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.model.User;

public final class WorkloadRequestBuilder {

    private WorkloadRequestBuilder(){

    }

    public static TrainerWorkloadRequest from(Training training, ActionType actionType) {
        User user = training.getTrainer().getUser();

        return TrainerWorkloadRequest.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(training.getTrainer().isActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType)
                .build();
    }
}
