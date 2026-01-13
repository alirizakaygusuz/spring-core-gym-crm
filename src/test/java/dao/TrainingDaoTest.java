package dao;

import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoTest {
    private Map<Long, Training> trainingStorage;
    private TrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        trainingStorage = new HashMap<>();
        trainingDao = new TrainingDao();
        trainingDao.setTrainingStorage(trainingStorage);
    }

    @Test
    void save_shouldAssignIdAndSaveTrainingWhenCalled() {
        Training training = new Training();
        training.setTrainingName("Strength Training");

        Training savedTraining = trainingDao.save(training);

        assertNotNull(savedTraining);
        assertEquals(1L, savedTraining.getId());
        assertEquals(1, trainingStorage.size());
        assertSame(savedTraining, trainingStorage.get(1L));

    }

    @Test
    void findById_shouldReturnTrainingWhenIdExists() {
        Training training = new Training();
        training.setId(1L);
        trainingStorage.put(1L, training);

        var result = trainingDao.findById(1L);

        assertTrue(result.isPresent());
        assertSame(training, result.get());
    }

    @Test
    void findById_shouldReturnEmptyWhenIdNotExists() {
        var result = trainingDao.findById(99L);
        assertTrue(result.isEmpty());

    }


    @Test
    void getAll_shouldReturnAllTrainings() {
        Training training1 = new Training();
        training1.setId(1L);
        trainingStorage.put(1L, training1);

        Training training2 = new Training();
        training2.setId(2L);
        trainingStorage.put(2L, training2);

        Map<Long, Training> allTrainings = trainingDao.getAll();

        assertEquals(2, allTrainings.size());
        assertSame(training1, allTrainings.get(1L));
        assertSame(training2, allTrainings.get(2L));
    }
}
