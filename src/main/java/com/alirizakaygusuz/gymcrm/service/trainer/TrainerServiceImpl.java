package com.alirizakaygusuz.gymcrm.service.trainer;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.training.TrainerTrainingFilterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainingMapper;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.model.TrainingType;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.user.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import com.alirizakaygusuz.gymcrm.service.validator.SelfAccessValidator;
import com.alirizakaygusuz.gymcrm.service.validator.TrainingDateRangeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingDao trainingDao;

    private final UserService userService;

    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    private final SelfAccessValidator selfAccessValidator;

    private final CommonValidator commonValidator;
    private final TrainingDateRangeValidator trainingDateRangeValidator;


    @Override
    @Transactional
    public TrainerRegisterResponse register(TrainerRegisterRequest request) {
        log.info("Starting trainer profile creation");
        User savedUser = userService.createUserWithCredentials(request);

        log.info("User profile created with username={}", savedUser.getUsername());

        TrainingType specialization = resolveSpecialization(request.specializationId());

        Trainer trainer = buildTrainerForCreate(savedUser, specialization);
        Trainer saved = trainerDao.save(trainer);

        log.info("Trainer profile created. trainerId={}, username={}",
                saved.getId(), savedUser.getUsername());

        return trainerMapper.toRegisterResponse(saved.getUser());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerProfileResponse getProfile(String currentUsername, String targetUsername) {
        log.info("Starting trainer profile selection");

        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        log.info("Selecting trainer profile with username={}", targetUsername);

        Trainer selectedTrainer = findTrainerWithDetailsByUsernameOrThrow(targetUsername);

        return trainerMapper.toProfileResponse(selectedTrainer);

    }

    @Override
    @Transactional
    public TrainerProfileUpdateResponse updateProfile(
            String currentUsername,
            String targetUsername,
            TrainerProfileUpdateRequest request
    ) {

        log.info("Updating trainer profile. username={}", targetUsername);
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        Trainer trainer = findTrainerWithDetailsByUsernameOrThrow(targetUsername);

        userService.applyProfileUpdate(trainer.getUser(), request);

        if (request.specializationId() != null) {
            TrainingType specialization = resolveSpecialization(request.specializationId());
            trainer.setSpecialization(specialization);
        }

        Trainer updatedTrainer = trainerDao.update(trainer);

        log.info("Trainer profile updated. trainerId={}, username={}",
                updatedTrainer.getId(), updatedTrainer.getUser().getUsername());

        return trainerMapper.toProfileUpdateResponse(updatedTrainer);
    }

    @Override
    public List<TrainerTrainingFilterResponse> getTrainings(
            String currentUsername,
            String targetUsername,
            TrainerTrainingFilterRequest filters
    ) {
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);

        trainingDateRangeValidator.validateDateRange(filters.periodFrom(), filters.periodTo());

        trainerDao.findByUsername(targetUsername)
                .orElseThrow(() -> new AuthenticationFailedException("Only trainer can access trainer trainings list"));

        List<Training> trainings = trainingDao.findTrainerTrainingsByCriteria(
                targetUsername,
                filters.periodFrom(),
                filters.periodTo(),
                filters.traineeName()
        );

        return trainings.stream()
                .map(trainingMapper::toTrainerTrainingFilterResponse)
                .toList();

    }


    @Override
    public void setActiveStatus(String currentUsername, String targetUsername, boolean isActive) {
        selfAccessValidator.assertSelfAccess(currentUsername, targetUsername);
        Trainer trainer = findTrainerByUsernameOrThrow(targetUsername);
        userService.setActiveStatus(trainer.getUser(), isActive);
    }


    private Trainer findTrainerByUsernameOrThrow(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainer",
                        "username",
                        username));
    }

    private Trainer findTrainerWithDetailsByUsernameOrThrow(String username) {
        return trainerDao.findByUsernameWithDetails(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainer",
                        "username",
                        username));
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