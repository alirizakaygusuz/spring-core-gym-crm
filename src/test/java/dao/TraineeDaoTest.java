package dao;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {
    private Map<Long, Trainee> traineeStorage;

    private TraineeDao traineeDao;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();
        traineeDao = new TraineeDao();
        traineeDao.setTraineeStorage(traineeStorage);
    }


    @DisplayName("save should save trainee and assign id when called")
    @Test
    void save_shouldAssignIdAndSaveTraineeWhenCalled() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");

        Trainee savedTrainee = traineeDao.save(trainee);


        assertNotNull(savedTrainee);
        assertEquals(1L, savedTrainee.getId());
        assertEquals(1, traineeStorage.size());
        assertSame(savedTrainee, traineeStorage.get(1L));

    }


    @DisplayName("findById should return trainee when id exists")
    @Test
    void findById_shouldReturnTraineeWhenIdExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        traineeStorage.put(1L, trainee);

        var result = traineeDao.findById(1L);

        assertTrue(result.isPresent());
        assertSame(trainee, result.get());
    }


    @DisplayName("findById should return empty if not exists")
    @Test
    void findById_shouldReturnEmptyWhenIdNotExists() {
        var result = traineeDao.findById(99L);

        assertTrue(result.isEmpty());

    }

    @DisplayName("findByUsername should return trainee when username exists")
    @Test
    void findByUsername_shouldReturnTraineeWhenUsernameExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("john.doe");
        traineeStorage.put(1L, trainee);

        var result = traineeDao.findByUsername("john.doe");

        assertTrue(result.isPresent());
        assertSame(trainee, result.get());
    }

    @DisplayName("findByUsername should return empty when username not exists")
    @Test
    void findByUsername_shouldReturnEmptyWhenUsernameNotExists() {
        var result = traineeDao.findByUsername("not_exists_username");

        assertFalse(result.isPresent());
    }

    @DisplayName("findByUsername should return empty when username is null")
    @Test
    void findByUsername_shouldReturnEmptyWhenUsernameIsNull() {
        var result = traineeDao.findByUsername(null);

        assertFalse(result.isPresent());
    }

    @DisplayName("getAll should return all trainees when called")
    @Test
    void getAll_shouldReturnAllTraineesWhenCalled() {
        Trainee trainee1 = new Trainee();
        trainee1.setId(1L);
        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        traineeStorage.put(1L, trainee1);
        traineeStorage.put(2L, trainee2);

        Map<Long, Trainee> result = traineeDao.getAll();

        assertEquals(2, result.size());
        assertSame(trainee1, result.get(1L));
        assertSame(trainee2, result.get(2L));

    }


    @DisplayName("update should update when trainee is exists")
    @Test
    void update_shouldUpdateTraineeWhenIdAndTraineeAreValid() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        traineeStorage.put(1L, trainee);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setFirstName("UpdatedName");
        Trainee result = traineeDao.update(1L, updatedTrainee);

        assertEquals(1L, result.getId());
        assertEquals("UpdatedName", result.getFirstName());
        assertSame(result, traineeStorage.get(1L));
    }

    @DisplayName("delete should remove trainee when id exists")
    @Test
    void delete_shouldRemoveTraineeWhenIdExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        traineeStorage.put(1L, trainee);
        traineeDao.delete(1L);
        assertFalse(traineeStorage.containsKey(1L));

    }


    @DisplayName("existsByUsername should return true when username exists")
    @Test
    void existsByUsername_shouldReturnTrueWhenUsernameExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("john.doe");
        traineeStorage.put(1L, trainee);

        boolean exists = traineeDao.existsByUsername("john.doe");

        assertTrue(exists);
    }

    @DisplayName("existsByUsername should return false when username not exists")
    @Test
    void existsByUsername_shouldReturnFalseWhenUsernameNotExists() {
        boolean exists = traineeDao.existsByUsername("not_exists_username.existent");
        assertFalse(exists);
    }

    @DisplayName("existsByUsername should return false when username is null")
    @Test
    void existsByUsername_shouldReturnFalseWhenUsernameIsNull() {
        boolean exists = traineeDao.existsByUsername(null);
        assertFalse(exists);
    }


}
