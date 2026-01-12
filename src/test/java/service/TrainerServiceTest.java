package service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.TrainingType;
import com.alirizakaygusuz.gymcrm.service.TrainerService;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    TrainerDao trainerDao;

    @Mock
    CredentialsGenerator credentialsGenerator;

    @Mock
    UserValidator userValidator;

    @InjectMocks
    TrainerService trainerService;


    @BeforeEach
    void setUp() {
        trainerService.setCredentialsGenerator(credentialsGenerator);
        trainerService.setUserValidator(userValidator);
    }

    @DisplayName("createProfile should create Trainer when Trainer is valid")
    @Test
    void createProfile_ShouldCreateTrainerWhenTrainerIsValid() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        when(credentialsGenerator.generateUniqueUsername("John", "Doe"))
                .thenReturn("johndoe");
        when(credentialsGenerator.generateRandomPassword())
                .thenReturn("randomPassword123");

        Trainer savedTrainer = new Trainer();
        savedTrainer.setId(1L);
        savedTrainer.setFirstName("John");
        savedTrainer.setLastName("Doe");
        savedTrainer.setUsername("johndoe");
        savedTrainer.setPassword("randomPassword123");


        when(trainerDao.save(savedTrainer)).thenReturn(savedTrainer);
        Trainer createdTrainer = trainerService.createProfile(trainer);


        assertNotNull(createdTrainer);
        assertEquals(1L, createdTrainer.getId());
        assertEquals("johndoe", createdTrainer.getUsername());
        assertEquals("randomPassword123", createdTrainer.getPassword());


        verify(userValidator).validateUser(trainer);
        verify(credentialsGenerator).generateUniqueUsername("John", "Doe");
        verify(credentialsGenerator).generateRandomPassword();
        verify(trainerDao).save(argThat(t ->
                t.getFirstName().equals("John") &&
                        t.getLastName().equals("Doe") &&
                        t.getUsername().equals("johndoe") &&
                        t.getPassword().equals("randomPassword123")
        ));

        verifyNoMoreInteractions(trainerDao, credentialsGenerator, userValidator);

    }

    //Test createProfile method when user is invalid and should throw exception
    @DisplayName("createProfile should throw exception when Trainer is invalid")
    @Test
    void createProfile_ShouldThrowExceptionWhenTrainerIsInvalid() {
        Trainer trainer = new Trainer();
        trainer.setFirstName(" ");
        trainer.setLastName("Doe");

        doThrow(new IllegalArgumentException("First name cannot be null or blank"))
                .when(userValidator).validateUser(trainer);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createProfile(trainer);
        });

        assertEquals("First name cannot be null or blank", exception.getMessage());
        verify(userValidator).validateUser(trainer);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);

    }

    //Test createProfile method when Trainer is null and should throw exception
    @DisplayName("createProfile should throw exception when Trainer is null")
    @Test
    void createProfile_ShouldThrowExceptionWhenTrainerIsNull() {
        Trainer nullUser = null;

        doThrow(new IllegalArgumentException("User cannot be null"))
                .when(userValidator).validateUser(nullUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createProfile(nullUser);
        });
        assertEquals("User cannot be null", exception.getMessage());

        verify(userValidator).validateUser(nullUser);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


    //Test selectProfile by id when id is valid
    @DisplayName("selectProfile should return selected Trainer when id is valid")
    @Test
    void selectProfile_shouldReturnSelectedTrainerWhenIdIsValid() {
        Long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);
        trainer.setFirstName("Jane");
        trainer.setLastName("Doe");

        when(trainerDao.findById(trainerId)).thenReturn(Optional.of(trainer));

        Trainer foundTrainer = trainerService.selectProfile(trainerId);

        assertNotNull(foundTrainer);
        assertEquals(trainerId, foundTrainer.getId());
        assertEquals("Jane", foundTrainer.getFirstName());
        assertEquals("Doe", foundTrainer.getLastName());

        verify(userValidator).validateId(trainerId);
        verify(trainerDao).findById(trainerId);
        verifyNoMoreInteractions(trainerDao, userValidator);

    }


    //Test selectProfile by id when id is invalid and should throw exception
    @DisplayName("selectProfile should throw exception when id is invalid")
    @Test
    void selectProfile_ShouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;
        doThrow(new IllegalArgumentException("ID must be a positive number"))
                .when(userValidator).validateId(invalidId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.selectProfile(invalidId);
        });

        assertEquals("ID must be a positive number", exception.getMessage());

        verify(userValidator).validateId(invalidId);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


    //Test selectProfile by id when trainer not found and should throw exception
    @DisplayName("selectProfile should throw exception when trainer not found")
    @Test
    void selectProfile_ShouldThrowExceptionWhenTrainerNotFound() {
        Long trainerId = 2L;


        when(trainerDao.findById(trainerId)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainerService.selectProfile(trainerId);
        });

        assertEquals("Trainer not found with id: " + trainerId, exception.getMessage());

        verify(userValidator).validateId(trainerId);
        verify(trainerDao).findById(trainerId);
        verifyNoMoreInteractions(trainerDao, userValidator);
    }


    //Test selectProfile by username when username is valid
    @DisplayName("selectProfile should return selected Trainer when username is valid")
    @Test
    void selectProfile_shouldReturnSelectedTrainerWhenUsernameIsValid() {
        String username = "janedoe";
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Doe");
        trainer.setUsername(username);

        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));

        Trainer foundTrainer = trainerService.selectProfile(username);

        assertNotNull(foundTrainer);
        assertEquals(username, foundTrainer.getUsername());
        assertEquals("Jane", foundTrainer.getFirstName());
        assertEquals("Doe", foundTrainer.getLastName());

        verify(userValidator).validateUsername(username);
        verify(trainerDao).findByUsername(username);
        verifyNoMoreInteractions(trainerDao, userValidator);
    }


    //Test selectProfile by username when username is invalid and should throw exception
    @DisplayName("selectProfile should throw exception when username is invalid")
    @Test
    void selectProfile_ShouldThrowExceptionWhenUsernameIsInvalid() {
        String invalidUsername = "   ";
        doThrow(new IllegalArgumentException("Username cannot be null or blank"))
                .when(userValidator).validateUsername(invalidUsername);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.selectProfile(invalidUsername);
        });

        assertEquals("Username cannot be null or blank", exception.getMessage());

        verify(userValidator).validateUsername(invalidUsername);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


    //Test selectProfile by username when trainer not found and should throw exception
    @DisplayName("selectProfile should throw exception when trainer not found by username")
    @Test
    void selectProfile_ShouldThrowExceptionWhenTrainerNotFoundByUsername() {
        String username = "nonexistentuser";

        when(trainerDao.findByUsername(username)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainerService.selectProfile(username);
        });

        assertEquals("Trainer not found with username: " + username, exception.getMessage());

        verify(userValidator).validateUsername(username);
        verify(trainerDao).findByUsername(username);
        verifyNoMoreInteractions(trainerDao, userValidator);
    }


    //Test getAllTrainers should return all trainers
    @DisplayName("getAllTrainers should return all trainers")
    @Test
    void getAllTrainers_ShouldReturnAll() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setFirstName("Alice");
        trainer1.setLastName("Smith");

        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setFirstName("Bob");
        trainer2.setLastName("Johnson");

        Map<Long, Trainer> trainersMap = new HashMap<>();
        trainersMap.put(trainer1.getId(), trainer1);
        trainersMap.put(trainer2.getId(), trainer2);

        when(trainerDao.getAll()).thenReturn(trainersMap);

        Map<Long, Trainer> result = trainerService.getAllTrainers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));

        verify(trainerDao).getAll();
        verifyNoMoreInteractions(trainerDao, userValidator);

    }


    //Test happy path for updateProfile method
    @DisplayName("updateProfile should update Trainer when inputs are valid")
    @Test
    void updateProfile_ShouldUpdateTrainerWhenInputsAreValid() {
        Long trainerId = 1L;
        Trainer existingTrainer = new Trainer();
        existingTrainer.setId(trainerId);
        existingTrainer.setFirstName("OldFirstName");
        existingTrainer.setLastName("OldLastName");
        existingTrainer.setActive(true);
        existingTrainer.setSpecialization(TrainingType.STRENGTH);

        Trainer updatedInfo = new Trainer();
        updatedInfo.setFirstName("NewFirstName");
        updatedInfo.setLastName("NewLastName");
        updatedInfo.setActive(false);
        updatedInfo.setSpecialization(TrainingType.CARDIO);

        when(trainerDao.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
        when(trainerDao.update(eq(trainerId), existingTrainer)).thenAnswer(invocation -> invocation.getArgument(1));

        Trainer updatedTrainer = trainerService.updateProfile(trainerId, updatedInfo);

        assertNotNull(updatedTrainer);
        assertEquals(trainerId, updatedTrainer.getId());
        assertEquals("NewFirstName", updatedTrainer.getFirstName());
        assertEquals("NewLastName", updatedTrainer.getLastName());
        assertFalse(updatedTrainer.isActive());
        assertEquals(TrainingType.CARDIO, updatedTrainer.getSpecialization());

        verify(userValidator).validateId(trainerId);
        verify(userValidator).validateUser(updatedInfo);
        verify(trainerDao).findById(trainerId);
        verify(trainerDao).update(eq(trainerId), argThat(t ->
                t.getFirstName().equals("NewFirstName") &&
                        t.getLastName().equals("NewLastName") &&
                        !t.isActive() &&
                        t.getSpecialization() == TrainingType.CARDIO
        ));
        verifyNoMoreInteractions(trainerDao, userValidator);
        verifyNoInteractions(credentialsGenerator);
    }


    //Test updateProfile method when id is invalid and should throw exception
    @DisplayName("updateProfile should throw exception when id is invalid")
    @Test
    void updateProfile_ShouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -5L;
        Trainer updatedInfo = new Trainer();
        updatedInfo.setFirstName("NewFirstName");
        updatedInfo.setLastName("NewLastName");

        doThrow(new IllegalArgumentException("ID must be a positive number"))
                .when(userValidator).validateId(invalidId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.updateProfile(invalidId, updatedInfo);
        });

        assertEquals("ID must be a positive number", exception.getMessage());

        verify(userValidator).validateId(invalidId);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


    //Test updateProfile when Trainer is invalid and should throw exception
    @DisplayName("updateProfile should throw exception when Trainer is invalid")
    @Test
    void updateProfile_ShouldThrowExceptionWhenTrainerIsInvalid() {
        Long trainerId = 1L;
        Trainer invalidTrainer = new Trainer();
        invalidTrainer.setFirstName(" ");
        invalidTrainer.setLastName("NewLastName");

        doThrow(new IllegalArgumentException("First name cannot be null or blank"))
                .when(userValidator).validateUser(invalidTrainer);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.updateProfile(trainerId, invalidTrainer);
        });

        assertEquals("First name cannot be null or blank", exception.getMessage());

        verify(userValidator).validateId(trainerId);
        verify(userValidator).validateUser(invalidTrainer);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao);
    }


    //Test updateProfile when trainer not found should throw exception
    @DisplayName("updateProfile should throw exception when trainer not found")
    @Test
    void updateProfile_ShouldThrowExceptionWhenTrainerNotFound() {
        Long trainerId = 10L;
        Trainer updatedInfo = new Trainer();
        updatedInfo.setFirstName("NewFirstName");
        updatedInfo.setLastName("NewLastName");

        when(trainerDao.findById(trainerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainerService.updateProfile(trainerId, updatedInfo);
        });

        assertEquals("Trainer not found by id:" + trainerId, exception.getMessage());

        verify(userValidator).validateId(trainerId);
        verify(userValidator).validateUser(updatedInfo);
        verify(trainerDao).findById(trainerId);
        verifyNoMoreInteractions(trainerDao, userValidator);
    }


}
