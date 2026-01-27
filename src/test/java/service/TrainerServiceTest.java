package service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.exception.TrainerNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
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
    private TrainerDao trainerDao;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private TrainerService trainerService;


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


        when(trainerDao.save(trainer)).thenReturn(savedTrainer);
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

    @DisplayName("createProfile should throw exception when Trainer is invalid")
    @Test
    void createProfile_ShouldThrowExceptionWhenTrainerIsInvalid() {
        Trainer trainer = new Trainer();
        trainer.setFirstName(" ");
        trainer.setLastName("Doe");

        doThrow(new ValidationException("First name cannot be null or blank"))
                .when(userValidator).validateUser(trainer);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            trainerService.createProfile(trainer);
        });

        assertEquals("First name cannot be null or blank", exception.getMessage());
        verify(userValidator).validateUser(trainer);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);

    }

    @DisplayName("createProfile should throw exception when Trainer is null")
    @Test
    void createProfile_ShouldThrowExceptionWhenTrainerIsNull() {
        Trainer nullUser = null;

        doThrow(new ValidationException("User cannot be null"))
                .when(userValidator).validateUser(nullUser);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            trainerService.createProfile(nullUser);
        });
        assertEquals("User cannot be null", exception.getMessage());

        verify(userValidator).validateUser(nullUser);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


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


    @DisplayName("selectProfile should throw exception when id is invalid")
    @Test
    void selectProfile_ShouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;
        doThrow(new ValidationException("ID must be a positive number"))
                .when(userValidator).validateId(invalidId);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            trainerService.selectProfile(invalidId);
        });

        assertEquals("ID must be a positive number", exception.getMessage());

        verify(userValidator).validateId(invalidId);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


    @DisplayName("selectProfile should throw exception when trainer not found")
    @Test
    void selectProfile_ShouldThrowExceptionWhenTrainerNotFound() {
        Long trainerId = 2L;


        when(trainerDao.findById(trainerId)).thenReturn(java.util.Optional.empty());

        TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class, () -> {
            trainerService.selectProfile(trainerId);
        });

        assertEquals("Trainer not found with id: " + trainerId, exception.getMessage());

        verify(userValidator).validateId(trainerId);
        verify(trainerDao).findById(trainerId);
        verifyNoMoreInteractions(trainerDao, userValidator);
    }


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


    @DisplayName("selectProfile should throw exception when username is invalid")
    @Test
    void selectProfile_ShouldThrowExceptionWhenUsernameIsInvalid() {
        String invalidUsername = "   ";
        doThrow(new ValidationException("Username cannot be null or blank"))
                .when(userValidator).validateUsername(invalidUsername);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            trainerService.selectProfile(invalidUsername);
        });

        assertEquals("Username cannot be null or blank", exception.getMessage());

        verify(userValidator).validateUsername(invalidUsername);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


    @DisplayName("selectProfile should throw exception when trainer not found by username")
    @Test
    void selectProfile_ShouldThrowExceptionWhenTrainerNotFoundByUsername() {
        String username = "nonexistentuser";

        when(trainerDao.findByUsername(username)).thenReturn(java.util.Optional.empty());

        TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class, () -> {
            trainerService.selectProfile(username);
        });

        assertEquals("Trainer not found with username: " + username, exception.getMessage());

        verify(userValidator).validateUsername(username);
        verify(trainerDao).findByUsername(username);
        verifyNoMoreInteractions(trainerDao, userValidator);
    }


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


    @DisplayName("updateProfile should update Trainer when inputs are valid")
    @Test
    void updateProfile_ShouldUpdateTrainerWhenInputsAreValid() {
        Long trainerId = 1L;
        Trainer existingTrainer = new Trainer();
        existingTrainer.setId(trainerId);
        existingTrainer.setFirstName("OldFirstName");
        existingTrainer.setLastName("OldLastName");
        existingTrainer.setActive(true);
        existingTrainer.setSpecialization(TrainingTypeCode.STRENGTH);

        Trainer updatedInfo = new Trainer();
        updatedInfo.setFirstName("NewFirstName");
        updatedInfo.setLastName("NewLastName");
        updatedInfo.setActive(false);
        updatedInfo.setSpecialization(TrainingTypeCode.CARDIO);

        Trainer expectedUpdatedTrainer = new Trainer();
        expectedUpdatedTrainer.setId(trainerId);
        expectedUpdatedTrainer.setFirstName("NewFirstName");
        expectedUpdatedTrainer.setLastName("NewLastName");
        expectedUpdatedTrainer.setActive(false);
        expectedUpdatedTrainer.setSpecialization(TrainingTypeCode.CARDIO);


        when(trainerDao.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
        when(trainerDao.update(trainerId, existingTrainer)).thenReturn(expectedUpdatedTrainer);

        Trainer updatedTrainer = trainerService.updateProfile(trainerId, updatedInfo);

        assertNotNull(updatedTrainer);
        assertEquals(trainerId, updatedTrainer.getId());
        assertEquals("NewFirstName", updatedTrainer.getFirstName());
        assertEquals("NewLastName", updatedTrainer.getLastName());
        assertFalse(updatedTrainer.isActive());
        assertEquals(TrainingTypeCode.CARDIO, updatedTrainer.getSpecialization());

        verify(userValidator).validateId(trainerId);
        verify(userValidator).validateUser(updatedInfo);
        verify(trainerDao).findById(trainerId);
        verify(trainerDao).update(eq(trainerId), argThat(t ->
                t.getFirstName().equals("NewFirstName") &&
                        t.getLastName().equals("NewLastName") &&
                        !t.isActive() &&
                        t.getSpecialization() == TrainingTypeCode.CARDIO
        ));
        verifyNoMoreInteractions(trainerDao, userValidator);
        verifyNoInteractions(credentialsGenerator);
    }


    @DisplayName("updateProfile should throw exception when id is invalid")
    @Test
    void updateProfile_ShouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -5L;
        Trainer updatedInfo = new Trainer();
        updatedInfo.setFirstName("NewFirstName");
        updatedInfo.setLastName("NewLastName");

        doThrow(new ValidationException("ID must be a positive number"))
                .when(userValidator).validateId(invalidId);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            trainerService.updateProfile(invalidId, updatedInfo);
        });

        assertEquals("ID must be a positive number", exception.getMessage());

        verify(userValidator).validateId(invalidId);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao, credentialsGenerator);
    }


    @DisplayName("updateProfile should throw exception when Trainer is invalid")
    @Test
    void updateProfile_ShouldThrowExceptionWhenTrainerIsInvalid() {
        Long trainerId = 1L;
        Trainer invalidTrainer = new Trainer();
        invalidTrainer.setFirstName(" ");
        invalidTrainer.setLastName("NewLastName");

        doThrow(new ValidationException("First name cannot be null or blank"))
                .when(userValidator).validateUser(invalidTrainer);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            trainerService.updateProfile(trainerId, invalidTrainer);
        });

        assertEquals("First name cannot be null or blank", exception.getMessage());

        verify(userValidator).validateId(trainerId);
        verify(userValidator).validateUser(invalidTrainer);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(trainerDao);
    }


    @DisplayName("updateProfile should throw exception when trainer not found")
    @Test
    void updateProfile_ShouldThrowExceptionWhenTrainerNotFound() {
        Long trainerId = 10L;
        Trainer updatedInfo = new Trainer();
        updatedInfo.setFirstName("NewFirstName");
        updatedInfo.setLastName("NewLastName");

        when(trainerDao.findById(trainerId)).thenReturn(Optional.empty());

        TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class, () -> {
            trainerService.updateProfile(trainerId, updatedInfo);
        });

        assertEquals("Trainer not found with id: " + trainerId, exception.getMessage());

        verify(userValidator).validateId(trainerId);
        verify(userValidator).validateUser(updatedInfo);
        verify(trainerDao).findById(trainerId);
        verifyNoMoreInteractions(trainerDao, userValidator);
    }


}
