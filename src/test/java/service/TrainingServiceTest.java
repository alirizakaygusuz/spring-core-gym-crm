package service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TraineeTrainingQueryRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainerTrainingQueryRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingResponse;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.mapper.TrainingMapper;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.service.TrainingService;
import com.alirizakaygusuz.gymcrm.service.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private CommonValidator commonValidator;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingService trainingService;

    @DisplayName("addTraining should return TrainingResponse when request is valid and user is trainee")
    @Test
    void addTraining_shouldReturnTrainingResponseWhenRequestIsValidAndUserIsTrainee() {
        LoginRequest login = mock(LoginRequest.class);

        TrainingCreateRequest createRequest = new TrainingCreateRequest(
                2L,
                3L,
                "Bench Press",
                LocalDate.of(2026, 1, 10),
                60
        );

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(11L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(3L);

        Training savedTraining = new Training();
        savedTraining.setId(100L);
        savedTraining.setTrainee(trainee);
        savedTraining.setTrainer(trainer);
        savedTraining.setTrainingType(trainingType);
        savedTraining.setTrainingName("Bench Press");
        savedTraining.setTrainingDate(LocalDate.of(2026, 1, 10));
        savedTraining.setTrainingDuration(60);

        TrainingResponse response = mock(TrainingResponse.class);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findById(3L)).thenReturn(Optional.of(trainingType));
        when(trainingDao.save(any(Training.class))).thenReturn(savedTraining);
        when(trainingMapper.toCreateResponse(savedTraining)).thenReturn(response);

        TrainingResponse result = trainingService.addTraining(login, createRequest);

        assertNotNull(result);
        assertSame(response, result);

        verify(commonValidator).validateNotNull(login, "Login request");

        verify(commonValidator).validateNotNull(createRequest, "Add training request");
        verify(commonValidator).validateNotNull(2L, "Trainer ID");
        verify(commonValidator).validateNotNull(3L, "TrainingType ID");
        verify(commonValidator).validateNotBlank("Bench Press", "Training name");
        verify(commonValidator).validateNotNull(LocalDate.of(2026, 1, 10), "Training date");
        verify(commonValidator).validateNotNull(60, "Training duration");

        verify(userService).authenticate(login);

        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findById(2L);
        verify(trainingTypeDao).findById(3L);

        verify(trainingDao).save(argThat(t ->
                t.getTrainee() == trainee &&
                        t.getTrainer() == trainer &&
                        t.getTrainingType() == trainingType &&
                        "Bench Press".equals(t.getTrainingName()) &&
                        LocalDate.of(2026, 1, 10).equals(t.getTrainingDate()) &&
                        t.getTrainingDuration() == 60
        ));

        verify(trainingMapper).toCreateResponse(savedTraining);

        verifyNoMoreInteractions(
                commonValidator, userService, traineeDao, trainerDao, trainingTypeDao, trainingDao, trainingMapper
        );
    }

    @DisplayName("addTraining should throw ValidationException when trainingDuration is zero or negative")
    @Test
    void addTraining_shouldThrowValidationExceptionWhenTrainingDurationIsZeroOrNegative() {
        LoginRequest login = mock(LoginRequest.class);

        TrainingCreateRequest createRequest = new TrainingCreateRequest(
                2L,
                3L,
                "Bench Press",
                LocalDate.of(2026, 1, 10),
                0
        );

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> trainingService.addTraining(login, createRequest)
        );

        assertEquals("Training duration must be greater than zero", ex.getMessage());

        verify(commonValidator).validateNotNull(login, "Login request");

        verify(commonValidator).validateNotNull(createRequest, "Add training request");
        verify(commonValidator).validateNotNull(2L, "Trainer ID");
        verify(commonValidator).validateNotNull(3L, "TrainingType ID");
        verify(commonValidator).validateNotBlank("Bench Press", "Training name");
        verify(commonValidator).validateNotNull(LocalDate.of(2026, 1, 10), "Training date");
        verify(commonValidator).validateNotNull(0, "Training duration");

        verifyNoMoreInteractions(commonValidator);
        verifyNoInteractions(userService, traineeDao, trainerDao, trainingTypeDao, trainingDao, trainingMapper);
    }

    @DisplayName("addTraining should throw AuthenticationFailedException when authenticated user is not trainee")
    @Test
    void addTraining_shouldThrowAuthenticationFailedExceptionWhenUserIsNotTrainee() {
        LoginRequest login = mock(LoginRequest.class);

        TrainingCreateRequest createRequest = new TrainingCreateRequest(
                2L,
                3L,
                "Bench Press",
                LocalDate.of(2026, 1, 10),
                60
        );

        User authUser = new User();
        authUser.setUsername("trainer.user");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("trainer.user")).thenReturn(Optional.empty());

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> trainingService.addTraining(login, createRequest)
        );

        assertEquals("Only trainees can add trainings", ex.getMessage());

        verify(commonValidator).validateNotNull(login, "Login request");

        verify(commonValidator).validateNotNull(createRequest, "Add training request");
        verify(commonValidator).validateNotNull(2L, "Trainer ID");
        verify(commonValidator).validateNotNull(3L, "TrainingType ID");
        verify(commonValidator).validateNotBlank("Bench Press", "Training name");
        verify(commonValidator).validateNotNull(LocalDate.of(2026, 1, 10), "Training date");
        verify(commonValidator).validateNotNull(60, "Training duration");

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("trainer.user");

        verifyNoMoreInteractions(commonValidator, userService, traineeDao);
        verifyNoInteractions(trainerDao, trainingTypeDao, trainingDao, trainingMapper);
    }

    @DisplayName("addTraining should throw ResourceNotFoundException when trainer not found")
    @Test
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        LoginRequest login = mock(LoginRequest.class);

        TrainingCreateRequest createRequest = new TrainingCreateRequest(
                999L,
                3L,
                "Bench Press",
                LocalDate.of(2026, 1, 10),
                60
        );

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(11L);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(login, createRequest)
        );

        assertEquals("Trainer not found with id : '999'", ex.getMessage());

        verify(commonValidator).validateNotNull(login, "Login request");

        verify(commonValidator).validateNotNull(createRequest, "Add training request");
        verify(commonValidator).validateNotNull(999L, "Trainer ID");
        verify(commonValidator).validateNotNull(3L, "TrainingType ID");
        verify(commonValidator).validateNotBlank("Bench Press", "Training name");
        verify(commonValidator).validateNotNull(LocalDate.of(2026, 1, 10), "Training date");
        verify(commonValidator).validateNotNull(60, "Training duration");

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findById(999L);

        verifyNoMoreInteractions(commonValidator, userService, traineeDao, trainerDao);
        verifyNoInteractions(trainingTypeDao, trainingDao, trainingMapper);
    }

    @DisplayName("addTraining should throw ResourceNotFoundException when training type not found")
    @Test
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTrainingTypeNotFound() {
        LoginRequest login = mock(LoginRequest.class);

        TrainingCreateRequest createRequest = new TrainingCreateRequest(
                2L,
                777L,
                "Bench Press",
                LocalDate.of(2026, 1, 10),
                60
        );

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(11L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findById(777L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(login, createRequest)
        );

        assertEquals("TrainingType not found with id : '777'", ex.getMessage());

        verify(commonValidator).validateNotNull(login, "Login request");

        verify(commonValidator).validateNotNull(createRequest, "Add training request");
        verify(commonValidator).validateNotNull(2L, "Trainer ID");
        verify(commonValidator).validateNotNull(777L, "TrainingType ID");
        verify(commonValidator).validateNotBlank("Bench Press", "Training name");
        verify(commonValidator).validateNotNull(LocalDate.of(2026, 1, 10), "Training date");
        verify(commonValidator).validateNotNull(60, "Training duration");

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findById(2L);
        verify(trainingTypeDao).findById(777L);

        verifyNoMoreInteractions(commonValidator, userService, traineeDao, trainerDao, trainingTypeDao);
        verifyNoInteractions(trainingDao, trainingMapper);
    }



    @DisplayName("getTraineeTrainings should throw ValidationException when from date is after to date")
    @Test
    void getTraineeTrainings_shouldThrowValidationExceptionWhenFromDateIsAfterToDate() {
        LoginRequest login = mock(LoginRequest.class);

        TraineeTrainingQueryRequest query = new TraineeTrainingQueryRequest(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 1, 1),
                null,
                null
        );

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> trainingService.getTraineeTrainings(login, query)
        );

        assertEquals("'from' date must be less than or equal to 'to' date", ex.getMessage());

        verify(commonValidator).validateNotNull(query, "Trainee training query request");
        verifyNoMoreInteractions(commonValidator);

        verifyNoInteractions(userService, traineeDao, trainerDao, trainingTypeDao, trainingDao, trainingMapper);
    }

    @DisplayName("getTraineeTrainings should throw AuthenticationFailedException when authenticated user is not trainee")
    @Test
    void getTraineeTrainings_shouldThrowAuthenticationFailedExceptionWhenUserIsNotTrainee() {
        LoginRequest login = mock(LoginRequest.class);

        TraineeTrainingQueryRequest query = new TraineeTrainingQueryRequest(
                null,
                null,
                null,
                null
        );

        User authUser = new User();
        authUser.setUsername("trainer.user");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("trainer.user")).thenReturn(Optional.empty());

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> trainingService.getTraineeTrainings(login, query)
        );

        assertEquals("Only trainee can access trainee trainings list", ex.getMessage());

        verify(commonValidator).validateNotNull(query, "Trainee training query request");
        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("trainer.user");

        verifyNoMoreInteractions(commonValidator, userService, traineeDao);
        verifyNoInteractions(trainerDao, trainingTypeDao, trainingDao, trainingMapper);
    }


    @DisplayName("getTrainerTrainings should throw ValidationException when from date is after to date")
    @Test
    void getTrainerTrainings_shouldThrowValidationExceptionWhenFromDateIsAfterToDate() {
        LoginRequest login = mock(LoginRequest.class);

        TrainerTrainingQueryRequest query = new TrainerTrainingQueryRequest(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 1, 1),
                null
        );

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> trainingService.getTrainerTrainings(login, query)
        );

        assertEquals("'from' date must be less than or equal to 'to' date", ex.getMessage());

        verify(commonValidator).validateNotNull(query, "Trainer training query request");
        verifyNoMoreInteractions(commonValidator);

        verifyNoInteractions(userService, traineeDao, trainerDao, trainingTypeDao, trainingDao, trainingMapper);
    }

    @DisplayName("getTrainerTrainings should throw AuthenticationFailedException when authenticated user is not trainer")
    @Test
    void getTrainerTrainings_shouldThrowAuthenticationFailedExceptionWhenUserIsNotTrainer() {
        LoginRequest login = mock(LoginRequest.class);

        TrainerTrainingQueryRequest query = new TrainerTrainingQueryRequest(
                null,
                null,
                null
        );

        User authUser = new User();
        authUser.setUsername("john.doe");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> trainingService.getTrainerTrainings(login, query)
        );

        assertEquals("Only trainer can access trainer trainings list", ex.getMessage());

        verify(commonValidator).validateNotNull(query, "Trainer training query request");
        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");

        verifyNoMoreInteractions(commonValidator, userService, trainerDao);
        verifyNoInteractions(traineeDao, trainingTypeDao, trainingDao, trainingMapper);
    }
}
