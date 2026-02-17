package service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingTypeResponse;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.monitoring.metrics.AppMetrics;
import com.alirizakaygusuz.gymcrm.service.training.TrainingServiceImpl;
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
    private AppMetrics appMetrics;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    @DisplayName("addTraining should create training when all entities exist")
    void addTraining_shouldCreateTrainingWhenAllEntitiesExist() {
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

        assertDoesNotThrow(() -> trainingService.addTraining(request));

        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findByUsername("trainer.jane");
        verify(trainingTypeDao).findByName("CARDIO");
        verify(trainingDao).save(any(Training.class));
        verifyNoMoreInteractions(traineeDao, trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    @DisplayName("addTraining should throw ResourceNotFoundException when trainee not found")
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.jane",
                "CARDIO",
                LocalDate.of(2024, 6, 15),
                60
        );

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(request)
        );

        assertTrue(ex.getMessage().contains("Trainee"));
        assertTrue(ex.getMessage().contains("john.doe"));

        verify(traineeDao).findByUsername("john.doe");
        verifyNoMoreInteractions(traineeDao);
        verifyNoInteractions(trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    @DisplayName("addTraining should throw ResourceNotFoundException when trainer not found")
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
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

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(request)
        );

        assertTrue(ex.getMessage().contains("Trainer"));
        assertTrue(ex.getMessage().contains("trainer.jane"));

        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findByUsername("trainer.jane");
        verifyNoMoreInteractions(traineeDao, trainerDao);
        verifyNoInteractions(trainingTypeDao, trainingDao);
    }

    @Test
    @DisplayName("addTraining should throw ResourceNotFoundException when training type not found")
    void addTraining_shouldThrowResourceNotFoundExceptionWhenTrainingTypeNotFound() {
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

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.addTraining(request)
        );

        assertTrue(ex.getMessage().contains("TrainingType"));
        assertTrue(ex.getMessage().contains("CARDIO"));

        verify(traineeDao).findByUsername("john.doe");
        verify(trainerDao).findByUsername("trainer.jane");
        verify(trainingTypeDao).findByName("CARDIO");
        verifyNoMoreInteractions(traineeDao, trainerDao, trainingTypeDao);
        verifyNoInteractions(trainingDao);
    }

    @Test
    @DisplayName("getTrainingTypes should return mapped list")
    void getTrainingTypes_shouldReturnMappedList() {
        TrainingType t1 = new TrainingType();
        t1.setId(1L);
        t1.setTrainingTypeName(TrainingTypeCode.CARDIO);

        TrainingType t2 = new TrainingType();
        t2.setId(2L);
        t2.setTrainingTypeName(TrainingTypeCode.STRENGTH);

        when(trainingTypeDao.findAll()).thenReturn(List.of(t1, t2));

        List<TrainingTypeResponse> result = trainingService.getTrainingTypes();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals(TrainingTypeCode.CARDIO, result.get(0).trainingType());

        assertEquals(2L, result.get(1).id());
        assertEquals(TrainingTypeCode.STRENGTH, result.get(1).trainingType());

        verify(trainingTypeDao).findAll();
        verifyNoMoreInteractions(trainingTypeDao);
        verifyNoInteractions(traineeDao, trainerDao, trainingDao);
    }
}
