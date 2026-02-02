package com.alirizakaygusuz.gymcrm.service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerCreateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.TrainingType;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final UserService userService;
    private final TrainerMapper trainerMapper;
    private final CommonValidator commonValidator;


    @Transactional
    public TrainerCreateResponse createProfile(TrainerProfileRequest request) {

        log.info("Starting trainer profile creation");
        commonValidator.validateNotNull(request, "Trainer profile creation request");


        TrainingType specialization = resolveSpecialization(request.specializationId());

        User savedUser = userService.createUserWithCredentials(request);

        Trainer trainer = buildTrainerForCreate(savedUser, specialization);
        Trainer saved = trainerDao.save(trainer);

        log.info("Trainer profile created. trainerId={}, username={}",
                saved.getId(), savedUser.getUsername());

        return trainerMapper.toCreateResponse(saved);
    }


    @Transactional(readOnly = true)
    public TrainerProfileResponse selectProfile(LoginRequest request) {
        log.info("Starting trainer profile selection");

        User authUser = userService.authenticate(request);

        log.info("Selecting trainer profile with username={}", authUser.getUsername());

        Trainer selectedTrainer = findTrainerByUsernameOrThrow(authUser.getUsername());

        return trainerMapper.toProfileResponse(selectedTrainer);
    }


    @Transactional
    public void changePassword(LoginRequest request, String newPassword) {

        User authUser = authenticateAndValidateTrainer(request);
        log.info("Starting password change for trainer with username={}", authUser.getUsername());

        userService.changePassword(authUser, newPassword);
        log.info("Password changed for trainer with username={}", authUser.getUsername());
    }

    @Transactional
    public void activateTrainer(LoginRequest request) {
        User user = authenticateAndValidateTrainer(request);
        userService.activate(user);
    }

    @Transactional
    public void deactivateTrainer(LoginRequest request) {
        User user = authenticateAndValidateTrainer(request);
        userService.deactivate(user);
    }

    @Transactional
    public TrainerProfileResponse updateTrainerProfile(
            LoginRequest request,
            TrainerProfileRequest updateRequest
    ) {
        User authUser = userService.authenticate(request);

        log.info("Updating trainer profile. username={}", authUser.getUsername());

        Trainer trainer = findTrainerByUsernameOrThrow(authUser.getUsername());

        userService.applyProfileUpdate(trainer.getUser(), updateRequest);

        if (updateRequest.specializationId() != null) {
            TrainingType specialization = resolveSpecialization(updateRequest.specializationId());
            trainer.setSpecialization(specialization);
        }

        Trainer updatedTrainer = trainerDao.update(trainer);

        log.info("Trainer profile updated. trainerId={}, username={}",
                updatedTrainer.getId(), updatedTrainer.getUser().getUsername());

        return trainerMapper.toProfileResponse(updatedTrainer);
    }


    private Trainer findTrainerByUsernameOrThrow(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainer",
                        "username",
                        username));
    }


    private User authenticateAndValidateTrainer(LoginRequest request) {
        User authUser = userService.authenticate(request);
        findTrainerByUsernameOrThrow(authUser.getUsername());
        return authUser;
    }

    private Trainer buildTrainerForCreate(User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    private TrainingType resolveSpecialization(Long specializationId) {

        commonValidator.validateNotNull(specializationId, "Specialization ID");

        return trainingTypeDao.findById(specializationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "TrainingType",
                                "id",
                                specializationId
                        )
                );
    }


}