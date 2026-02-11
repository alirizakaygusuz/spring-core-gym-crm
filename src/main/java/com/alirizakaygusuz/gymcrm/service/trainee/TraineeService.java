package com.alirizakaygusuz.gymcrm.service.trainee;

import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.training.TraineeTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;

import java.util.List;

public interface TraineeService {

    TraineeRegisterResponse register(TraineeRegisterRequest request);

    TraineeProfileResponse getProfile(String currentUsername,
                                      String targetUsername);

    TraineeProfileUpdateResponse updateProfile(String currentUsername,
                                               String targetUsername ,
                                               TraineeProfileUpdateRequest request);

    void deleteProfile(String currentUsername,
                       String targetUsername);

    List<TrainerProfileSummaryResponse> getNotAssignedActiveTrainers(
            String currentUsername,
            String targetUsername
    );

    List<TrainerProfileSummaryResponse> updateTrainerList(
            String currentUsername,
            String targetUsername,
            List<String> trainerUsernames
    );


    List<TraineeTrainingFilterResponse> getTrainings(String currentUsername,
                                                     String targetUsername,
                                                     TraineeTrainingFilterRequest filters);

    void setActiveStatus(
            String currentUsername,
            String targetUsername,
            boolean isActive
    );

}
