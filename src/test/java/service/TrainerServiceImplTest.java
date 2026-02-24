package service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.monitoring.metrics.AppMetrics;
import com.alirizakaygusuz.gymcrm.service.trainer.TrainerServiceImpl;
import com.alirizakaygusuz.gymcrm.service.user.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;


    @Mock
    private UserService userService;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private CommonValidator commonValidator;

    @Mock
    private AppMetrics appMetrics;

    @InjectMocks
    private TrainerServiceImpl trainerService;


    @Test
    @DisplayName("register should create trainer and return response")
    void register_shouldCreateTrainerAndReturnResponse() {
        TrainerRegisterRequest request = new TrainerRegisterRequest(
                "Jane",
                "Smith",
                1L
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("Jane.Smith");
        savedUser.setPassword("encodedPassword");

        TrainingType specialization = new TrainingType();
        specialization.setId(1L);
        specialization.setTrainingTypeName(TrainingTypeCode.CARDIO);

        Trainer savedTrainer = new Trainer();
        savedTrainer.setId(1L);
        savedTrainer.setUser(savedUser);
        savedTrainer.setSpecialization(specialization);

        TrainerRegisterResponse response = new TrainerRegisterResponse("Jane.Smith", "password");

        when(userService.createUserWithCredentials(request, RoleType.TRAINER)).thenReturn(savedUser);
        when(trainingTypeDao.findById(1L)).thenReturn(Optional.of(specialization));
        when(trainerDao.save(any(Trainer.class))).thenReturn(savedTrainer);
        when(trainerMapper.toRegisterResponse(savedTrainer.getUser())).thenReturn(response);

        TrainerRegisterResponse result = trainerService.register(request);

        assertNotNull(result);
        assertEquals("Jane.Smith", result.username());

        verify(userService).createUserWithCredentials(request, RoleType.TRAINER);
        verify(commonValidator).validateNotNull(1L, "Specialization ID");
        verify(trainingTypeDao).findById(1L);
        verify(trainerDao).save(any(Trainer.class));
        verify(trainerMapper).toRegisterResponse(savedTrainer.getUser());
        verifyNoMoreInteractions(userService, commonValidator, trainingTypeDao, trainerDao, trainerMapper);
    }

    @Test
    @DisplayName("register should throw ResourceNotFoundException when specialization not found")
    void register_shouldThrowResourceNotFoundExceptionWhenSpecializationNotFound() {
        TrainerRegisterRequest request = new TrainerRegisterRequest(
                "Jane",
                "Smith",
                999L
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("Jane.Smith");

        when(userService.createUserWithCredentials(request, RoleType.TRAINER)).thenReturn(savedUser);
        when(trainingTypeDao.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.register(request)
        );

        assertTrue(exception.getMessage().contains("TrainingType"));

        verify(userService).createUserWithCredentials(request, RoleType.TRAINER);
        verify(commonValidator).validateNotNull(999L, "Specialization ID");
        verify(trainingTypeDao).findById(999L);
        verifyNoMoreInteractions(userService, commonValidator, trainingTypeDao);
        verifyNoInteractions(trainerDao, trainerMapper);
    }

    @Test
    @DisplayName("register should throw ValidationException when specializationId is null")
    void register_shouldThrowValidationExceptionWhenSpecializationIdIsNull() {
        TrainerRegisterRequest request = new TrainerRegisterRequest(
                "Jane",
                "Smith",
                null
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("Jane.Smith");

        when(userService.createUserWithCredentials(request, RoleType.TRAINER)).thenReturn(savedUser);
        doThrow(new ValidationException("Specialization ID cannot be null"))
                .when(commonValidator).validateNotNull(null, "Specialization ID");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> trainerService.register(request)
        );

        assertEquals("Specialization ID cannot be null", exception.getMessage());

        verify(userService).createUserWithCredentials(request, RoleType.TRAINER);
        verify(commonValidator).validateNotNull(null, "Specialization ID");
        verifyNoMoreInteractions(userService, commonValidator);
        verifyNoInteractions(trainingTypeDao, trainerDao, trainerMapper);
    }

    @Test
    @DisplayName("getProfile should return trainer profile when trainer exists")
    void getProfile_shouldReturnTrainerProfileWhenTrainerExists() {
        String username = "Jane.Smith";

        Trainer trainer = new Trainer();
        trainer.setId(1L);

        TrainerProfileResponse response = new TrainerProfileResponse(
                "Jane",
                "Smith",
                TrainingTypeCode.CARDIO,
                true,
                null
        );

        when(trainerDao.findByUsernameWithDetails(username)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(response);

        TrainerProfileResponse result = trainerService.getProfile(username);

        assertNotNull(result);
        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());

        verify(trainerDao).findByUsernameWithDetails(username);
        verify(trainerMapper).toProfileResponse(trainer);
        verifyNoMoreInteractions(trainerDao, trainerMapper);
    }

    @Test
    @DisplayName("getProfile should throw ResourceNotFoundException when trainer not found")
    void getProfile_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        String username = "Jane.Smith";

        when(trainerDao.findByUsernameWithDetails(username)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.getProfile(username)
        );

        assertTrue(exception.getMessage().contains("Trainer"));
        assertTrue(exception.getMessage().contains("username"));

        verify(trainerDao).findByUsernameWithDetails(username);
        verifyNoMoreInteractions(trainerDao);
        verifyNoInteractions(trainerMapper);
    }

    @Test
    @DisplayName("updateProfile should update trainer and return response")
    void updateProfile_shouldUpdateTrainerAndReturnResponse() {
        String username = "Jane.Smith";
        TrainerProfileUpdateRequest request = new TrainerProfileUpdateRequest(
                "Jane",
                "Smith",
                2L,
                true
        );

        User user = new User();
        user.setUsername("Jane.Smith");

        TrainingType newSpecialization = new TrainingType();
        newSpecialization.setId(2L);
        newSpecialization.setTrainingTypeName(TrainingTypeCode.STRENGTH);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);

        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setId(1L);
        updatedTrainer.setUser(user);
        updatedTrainer.setSpecialization(newSpecialization);

        TrainerProfileUpdateResponse response = new TrainerProfileUpdateResponse(
                "Jane.Smith",
                "Jane",
                "Smith",
                TrainingTypeCode.STRENGTH,
                true,
                null
        );

        when(trainerDao.findByUsernameWithDetails(username)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findById(2L)).thenReturn(Optional.of(newSpecialization));
        when(trainerDao.update(trainer)).thenReturn(updatedTrainer);
        when(trainerMapper.toProfileUpdateResponse(updatedTrainer)).thenReturn(response);

        TrainerProfileUpdateResponse result = trainerService.updateProfile(username, request);

        assertNotNull(result);
        assertEquals("Jane.Smith", result.username());
        assertEquals(TrainingTypeCode.STRENGTH, result.specialization());

        verify(trainerDao).findByUsernameWithDetails(username);
        verify(userService).applyProfileUpdate(user, request);
        verify(commonValidator).validateNotNull(2L, "Specialization ID");
        verify(trainingTypeDao).findById(2L);
        verify(trainerDao).update(trainer);
        verify(trainerMapper).toProfileUpdateResponse(updatedTrainer);
        verifyNoMoreInteractions(trainerDao, userService, commonValidator, trainingTypeDao, trainerMapper);
    }

    @Test
    @DisplayName("setActiveStatus should update trainer active status")
    void setActiveStatus_shouldUpdateTrainerActiveStatus() {
        String username = "Jane.Smith";

        User user = new User();
        user.setUsername("Jane.Smith");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.setActiveStatus(username, true));

        verify(trainerDao).findByUsername(username);
        verify(userService).setActiveStatus(user, true);
        verifyNoMoreInteractions(trainerDao, userService);
    }

    @Test
    @DisplayName("setActiveStatus should throw ResourceNotFoundException when trainer not found")
    void setActiveStatus_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        String username = "Jane.Smith";

        when(trainerDao.findByUsername(username)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.setActiveStatus(username, true)
        );

        assertTrue(exception.getMessage().contains("Trainer"));
        assertTrue(exception.getMessage().contains("username"));

        verify(trainerDao).findByUsername(username);
        verifyNoMoreInteractions(trainerDao);
        verifyNoInteractions(userService);
    }
}
