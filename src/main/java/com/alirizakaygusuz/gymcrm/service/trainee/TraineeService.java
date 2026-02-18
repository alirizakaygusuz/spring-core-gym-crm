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

    TraineeProfileResponse getProfile(String username);

    TraineeProfileUpdateResponse updateProfile(String username,
                                               TraineeProfileUpdateRequest request);

    void deleteProfile(String username);

    List<TrainerProfileSummaryResponse> getNotAssignedActiveTrainers(
            String username
    );

    List<TrainerProfileSummaryResponse> updateTrainerList(
            String username,
            List<String> trainerUsernames
    );


    List<TraineeTrainingFilterResponse> getTrainings(String username,
                                                     TraineeTrainingFilterRequest filters);

    void setActiveStatus(
            String username,
            boolean isActive
    );

}
