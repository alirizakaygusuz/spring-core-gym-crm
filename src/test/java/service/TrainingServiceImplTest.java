package service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.exception.AccessDeniedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.service.training.TrainingServiceImpl;
import com.alirizakaygusuz.gymcrm.service.validator.SelfAccessValidator;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private SelfAccessValidator selfAccessValidator;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    @DisplayName("addTraining should create training when all entities exist and access is allowed")
    void addTraining_shouldCreateTrainingWhenAllEntitiesExistAndAccessIsAllowed() {
        String currentUsername = "trainer.jane";
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.jane",
                "CARDIO",
                LocalDate.of(2024, 6, 15),
                60
        );

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(3L);
        trainingType.setTrainingTypeName(TrainingTypeCode.CARDIO);

        Training savedTraining = new Training();
        savedTraining.setId(10L);

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer.jane")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("CARDIO")).thenReturn(Optional.of(trainingType));
        when(trainingDao.save(any(Training.class))).thenReturn(savedTraining);

        assertDoesNotThrow(() -> trainingService.addTraining(currentUsername, request));

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.trainerUsername());
        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findByUsername("trainer.jane");
        verify(trainingTypeDao).findByName("CARDIO");
        verify(trainingDao).save(any(Training.class));
        verifyNoMoreInteractions(selfAccessValidator, traineeDao, trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    @DisplayName("addTraining should throw AccessDeniedException when trying to add training for another trainer")
    void addTraining_shouldThrowAccessDeniedExceptionWhenTryingToAddTrainingForAnotherTrainer() {
        String currentUsername = "trainer.john";
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.jane",
                "CARDIO",
                LocalDate.of(2024, 6, 15),
                60
        );

        doThrow(new AccessDeniedException("You can only access your own profile."))
                .when(selfAccessValidator).assertSelfAccess(currentUsername, request.trainerUsername());

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> trainingService.addTraining(currentUsername, request)
        );

        assertEquals("You can only access your own profile.", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.trainerUsername());
        verifyNoMoreInteractions(selfAccessValidator);
        verifyNoInteractions(traineeDao, trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    @DisplayName("addTraining should throw ResourceNotFoundException when trainee not found")
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        String currentUsername = "trainer.jane";
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.jane",
                "CARDIO",
                LocalDate.of(2024, 6, 15),
                60
        );

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(currentUsername, request)
        );

        assertEquals("Trainee not found with username : 'john.doe'", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.trainerUsername());
        verify(traineeDao).findByUsername("john.doe");
        verifyNoMoreInteractions(selfAccessValidator, traineeDao);
        verifyNoInteractions(trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    @DisplayName("addTraining should throw ResourceNotFoundException when trainer not found")
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        String currentUsername = "trainer.jane";
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.jane",
                "CARDIO",
                LocalDate.of(2024, 6, 15),
                60
        );

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer.jane")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(currentUsername, request)
        );

        assertEquals("Trainer not found with username : 'trainer.jane'", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.trainerUsername());
        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findByUsername("trainer.jane");
        verifyNoMoreInteractions(selfAccessValidator, traineeDao, trainerDao);
        verifyNoInteractions(trainingTypeDao, trainingDao);
    }

    @Test
    @DisplayName("addTraining should throw ResourceNotFoundException when training type not found")
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTrainingTypeNotFound() {
        String currentUsername = "trainer.jane";
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.jane",
                "CARDIO",
                LocalDate.of(2024, 6, 15),
                60
        );

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer.jane")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("CARDIO")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(currentUsername, request)
        );

        assertEquals("TrainingType not found with name : 'CARDIO'", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, request.trainerUsername());
        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findByUsername("trainer.jane");
        verify(trainingTypeDao).findByName("CARDIO");
        verifyNoMoreInteractions(selfAccessValidator, traineeDao, trainerDao, trainingTypeDao);
        verifyNoInteractions(trainingDao);
    }

    @Test
    @DisplayName("getTrainingTypes should return all training types")
    void getTrainingTypes_shouldReturnAllTrainingTypes() {
        TrainingType type1 = new TrainingType();
        type1.setId(1L);
        type1.setTrainingTypeName(TrainingTypeCode.CARDIO);

        TrainingType type2 = new TrainingType();
        type2.setId(2L);
        type2.setTrainingTypeName(TrainingTypeCode.STRENGTH);

        when(trainingTypeDao.findAll()).thenReturn(List.of(type1, type2));

        List<TrainingTypeResponse> result = trainingService.getTrainingTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(TrainingTypeCode.CARDIO, result.get(0).trainingType());
        assertEquals(2L, result.get(1).id());
        assertEquals(TrainingTypeCode.STRENGTH, result.get(1).trainingType());

        verify(trainingTypeDao).findAll();
        verifyNoMoreInteractions(trainingTypeDao);
        verifyNoInteractions(traineeDao, trainerDao, trainingDao, selfAccessValidator);
    }

    @Test
    @DisplayName("getTrainingTypes should return empty list when no training types exist")
    void getTrainingTypes_shouldReturnEmptyListWhenNoTrainingTypesExist() {
        when(trainingTypeDao.findAll()).thenReturn(List.of());

        List<TrainingTypeResponse> result = trainingService.getTrainingTypes();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(trainingTypeDao).findAll();
        verifyNoMoreInteractions(trainingTypeDao);
        verifyNoInteractions(traineeDao, trainerDao, trainingDao, selfAccessValidator);
    }
}