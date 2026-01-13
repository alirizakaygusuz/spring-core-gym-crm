package dao;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TrainerDaoTest {

    private Map<Long, Trainer> trainerStorage;
    private TrainerDao trainerDao;

    @BeforeEach
    void setUp() {
        trainerStorage = new HashMap<>();
        trainerDao = new TrainerDao();
        trainerDao.setTrainerStorage(trainerStorage);
    }

    @DisplayName("save should save trainer and assign id when called")
    @Test
    void save_shouldAssignIdAndSaveTrainerWhenCalled() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setUsername("Jane.Smith");

        Trainer savedTrainer = trainerDao.save(trainer);

        assertNotNull(savedTrainer);
        assertEquals(1L, savedTrainer.getId());
        assertEquals(1, trainerStorage.size());
        assertSame(savedTrainer, trainerStorage.get(1L));
    }

    @DisplayName("findById should return trainer when id exists")
    @Test
    void findById_shouldReturnTrainerWhenIdExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainerStorage.put(1L, trainer);

        var result = trainerDao.findById(1L);

        assertTrue(result.isPresent());
        assertSame(trainer, result.get());
    }

    @DisplayName("findById should return empty if not exists")
    @Test
    void findById_shouldReturnEmptyWhenIdNotExists() {
        var result = trainerDao.findById(99L);

        assertTrue(result.isEmpty());
    }


    @DisplayName("findByUsername should return trainer when username exists")
    @Test
    void findByUsername_shouldReturnTrainerWhenUsernameExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("Jane.Smith");
        trainerStorage.put(1L, trainer);

        var result = trainerDao.findByUsername("Jane.Smith");

        assertTrue(result.isPresent());
        assertSame(trainer, result.get());
    }

    @DisplayName("findByUsername should return empty when username not exists")
    @Test
    void findByUsername_shouldReturnEmptyWhenUsernameNotExists() {
        var result = trainerDao.findByUsername("not_exist_username");

        assertTrue(result.isEmpty());
    }

    @DisplayName("findByUsername should return empty when username is null")
    @Test
    void findByUsername_shouldReturnEmptyWhenUsernameIsNull() {
        var result = trainerDao.findByUsername(null);

        assertTrue(result.isEmpty());
    }


    @DisplayName("getAll should return all trainers when called")
    @Test
    void getAll_shouldReturnAllTrainersWhenCalled() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setUsername("trainer1");
        trainerStorage.put(1L, trainer1);

        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setUsername("trainer2");
        trainerStorage.put(2L, trainer2);

        Map<Long, Trainer> allTrainers = trainerDao.getAll();

        assertEquals(2, allTrainers.size());
        assertSame(trainer1, allTrainers.get(1L));
        assertSame(trainer2, allTrainers.get(2L));
    }

    @DisplayName("update should update trainer when id and trainer are valid")
    @Test
    void update_shouldUpdateTrainerWhenIdAndTrainerAreValid() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("oldUsername");
        trainerStorage.put(1L, trainer);

        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setUsername("newUsername");

        Trainer result = trainerDao.update(1L, updatedTrainer);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newUsername", result.getUsername());
        assertSame(result, trainerStorage.get(1L));
    }


    @DisplayName("existsByUsername should return true when username exists")
    @Test
    void existsByUsername_shouldReturnTrueWhenUsernameExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("existing.username");
        trainerStorage.put(1L, trainer);

        boolean exists = trainerDao.existsByUsername("existing.username");

        assertTrue(exists);
    }

    @DisplayName("existsByUsername should return false when username not exists")
    @Test
    void existsByUsername_shouldReturnFalseWhenUsernameNotExist() {
        boolean exists = trainerDao.existsByUsername("not_exist_username");

        assertFalse(exists);
    }

    @DisplayName("existsByUsername should return false when username is null")
    @Test
    void existsByUsername_shouldReturnFalseWhenUsernameIsNull() {
        boolean exists = trainerDao.existsByUsername(null);

        assertFalse(exists);
    }




}
