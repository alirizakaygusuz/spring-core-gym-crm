package service;

import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.model.TrainingType;
import com.alirizakaygusuz.gymcrm.service.TrainingService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    TrainingDao trainingDao;

    @Mock
    CommonValidator commonValidator;

    @InjectMocks
    TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService.setCommonValidator(commonValidator);
    }


    @DisplayName("create Profile should create Training Profile when Training is valid")
    @Test
    void createProfile_ShouldCreateTrainingProfileWhenTrainigIsValid() {
        Training training = new Training();
        training.setTrainingName("Yoga Basics");
        training.setTrainingType(TrainingType.CROSSFIT);

        Training saved = new Training();
        saved.setId(1L);
        saved.setTrainingName("Yoga Basics");
        saved.setTrainingType(TrainingType.CROSSFIT);

        when(trainingDao.save(training)).thenReturn(saved);

        Training createdTraining = trainingService.createProfile(training);

        assertNotNull(createdTraining);
        assertEquals(1L, createdTraining.getId());
        assertEquals("Yoga Basics", createdTraining.getTrainingName());
        assertEquals(TrainingType.CROSSFIT, createdTraining.getTrainingType());

        verify(commonValidator).validateNotBlank("Yoga Basics", "Training name");
        verify(trainingDao).save(training);
        verifyNoMoreInteractions(commonValidator, trainingDao);

    }


    @DisplayName("create Profile should throw Exception when Training is null")
    @Test
    void createProfile_ShouldThrowExceptionWhenTrainingIsNUll() {
        Training nullTraining = null;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            trainingService.createProfile(nullTraining);
        });

        assertEquals("Training object cannot be null", ex.getMessage());

        verifyNoInteractions(commonValidator, trainingDao);

    }

    @DisplayName("create Profile should throw Exception when Training name is invalid")
    @Test
    void createProfile_ShouldThrowExceptionWhenTrainingNameIsInvalid() {
        Training training = new Training();
        training.setTrainingName("   ");

        doThrow(new IllegalArgumentException("Training name cannot be null or blank"))
                .when(commonValidator).validateNotBlank(training.getTrainingName(), "Training name");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            trainingService.createProfile(training);
        });

        assertEquals("Training name cannot be null or blank", ex.getMessage());

        verify(commonValidator).validateNotBlank(training.getTrainingName(), "Training name");
        verifyNoInteractions(trainingDao);
        verifyNoMoreInteractions(commonValidator);
    }


    @DisplayName("select Profile should return Training when id is valid")
    @Test
    void selectProfile_ShouldReturnTrainingWhenIdIsValid() {
        Long trainingId = 1L;
        Training training = new Training();
        training.setId(trainingId);
        training.setTrainingName("Strength Training");

        when(trainingDao.findById(trainingId)).thenReturn(Optional.of(training));

        Training foundTraining = trainingService.selectProfile(trainingId);

        assertNotNull(foundTraining);
        assertEquals(trainingId, foundTraining.getId());
        assertEquals("Strength Training", foundTraining.getTrainingName());

        verify(commonValidator).validateId(trainingId);
        verify(trainingDao).findById(trainingId);

        verifyNoMoreInteractions(commonValidator, trainingDao);
    }

    @DisplayName("select Profile should throw Exception when id is invalid")
    @Test
    void selectProfile_ShouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;

        doThrow(new IllegalArgumentException("ID must be a positive number"))
                .when(commonValidator).validateId(invalidId);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            trainingService.selectProfile(invalidId);
        });

        assertEquals("ID must be a positive number", ex.getMessage());

        verify(commonValidator).validateId(invalidId);
        verifyNoInteractions(trainingDao);
        verifyNoMoreInteractions(commonValidator);
    }

    @DisplayName("select Profile should throw Exception when Training not found")
    @Test
    void selectProfile_ShouldThrowExceptionWhenTrainingNotFound() {
        Long trainingId = 2L;

        when(trainingDao.findById(trainingId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            trainingService.selectProfile(trainingId);
        });

        assertEquals("Training not found with id: " + trainingId, ex.getMessage());

        verify(commonValidator).validateId(trainingId);
        verify(trainingDao).findById(trainingId);

        verifyNoMoreInteractions(commonValidator, trainingDao);
    }


    @Test
    void getAllTrainings_ShouldReturnAllTrainings() {
        Map<Long, Training> trainingMap = new HashMap<>();

        Training training1 = new Training();
        training1.setId(1L);
        training1.setTrainingName("Cardio Blast");

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTrainingName("Strength Training");

        trainingMap.put(1L, training1);
        trainingMap.put(2L, training2);

        when(trainingDao.getAll()).thenReturn(trainingMap);

        Map<Long, Training> result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cardio Blast", result.get(1L).getTrainingName());
        assertEquals("Strength Training", result.get(2L).getTrainingName());

        verify(trainingDao).getAll();
        verifyNoMoreInteractions(trainingDao, commonValidator);
    }
}
