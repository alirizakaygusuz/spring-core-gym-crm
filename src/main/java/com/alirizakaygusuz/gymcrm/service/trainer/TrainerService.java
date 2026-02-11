package com.alirizakaygusuz.gymcrm.service.trainer;

import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateResponse;

import java.util.List;

public interface TrainerService {

    TrainerRegisterResponse register(TrainerRegisterRequest request);

    TrainerProfileResponse getProfile(String currentUsername ,
                                      String targetUsername);

    TrainerProfileUpdateResponse updateProfile(String currentUsername ,String targetUsername,
                                               TrainerProfileUpdateRequest request);

    List<TrainerTrainingFilterResponse> getTrainings(String currentUsername,
                                                     String targetUsername,
                                                     TrainerTrainingFilterRequest filters);

    void setActiveStatus(
            String currentUsername,
            String targetUsername,
            boolean isActive
    );
}
