package service;


import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.service.TraineeService;
import com.alirizakaygusuz.gymcrm.service.validator.UserValidator;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    TraineeDao traineeDao;

    @Mock
    CredentialsGenerator credentialsGenerator;

    @Mock
    UserValidator userValidator;

    @InjectMocks
    TraineeService traineeService;


    @BeforeEach
    void setUp() {
        traineeService.setCredentialsGenerator(credentialsGenerator);
        traineeService.setUserValidator(userValidator);
    }

    @DisplayName("createProfile should return created Trainee when Trainee is valid")
    @Test
    void createProfile_shouldReturnCreatedTraineeWhenTraineeIsValid() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setActive(true);

        when(credentialsGenerator.generateUniqueUsername("John", "Smith"))
                .thenReturn("john.smith");
        when(credentialsGenerator.generateRandomPassword())
                .thenReturn("randomPassword123");


        Trainee savedTrainee = new Trainee();
        savedTrainee.setId(1L);
        savedTrainee.setFirstName("John");
        savedTrainee.setLastName("Smith");
        savedTrainee.setUsername("john.smith");
        savedTrainee.setPassword("randomPassword123");
        savedTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        savedTrainee.setAddress("123 Main St");
        savedTrainee.setActive(true);


        when(traineeDao.save(trainee)).thenReturn(savedTrainee);


        Trainee createdTrainee = traineeService.createProfile(trainee);

        assertNotNull(createdTrainee);
        assertEquals("john.smith", createdTrainee.getUsername());
        assertEquals("randomPassword123", createdTrainee.getPassword());

        verify(userValidator).validateUser(trainee);
        verify(credentialsGenerator).generateRandomPassword();
        verify(credentialsGenerator).generateUniqueUsername("John", "Smith");
        verify(traineeDao).save(argThat(t ->
                t.getFirstName().equals("John") &&
                        t.getLastName().equals("Smith") &&
                        t.getUsername().equals("john.smith") &&
                        t.getPassword().equals("randomPassword123") &&
                        t.getDateOfBirth().equals(LocalDate.of(1990, 1, 1)) &&
                        t.getAddress().equals("123 Main St") &&
                        t.isActive()

        ));

        verifyNoMoreInteractions(traineeDao, credentialsGenerator, userValidator);

    }


    @DisplayName("createProfile should throw IllegalArgumentException when Trainee is invalid")
    @Test
    void createProfile_shouldThrowExceptionWhenTraineeIsInvalid() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("");
        trainee.setLastName("Smith");

        doThrow(new IllegalArgumentException("First name cannot be null or blank"))
                .when(userValidator).validateUser(trainee);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.createProfile(trainee)
        );

        assertEquals("First name cannot be null or blank", ex.getMessage());

        verify(userValidator).validateUser(trainee);

        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(traineeDao, credentialsGenerator);

    }


    @DisplayName("createProfile should throw IllegalArgumentException when Trainee is null")
    @Test
    void createProfile_shouldThrow_whenTraineeIsNull() {
        Trainee nullUser = null;

        doThrow(new IllegalArgumentException("User cannot be null"))
                .when(userValidator).validateUser(nullUser);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.createProfile(nullUser)
        );

        assertEquals("User cannot be null", ex.getMessage());

        verify(userValidator).validateUser(null);
        verifyNoInteractions(traineeDao, credentialsGenerator);
    }

    @DisplayName("selectProfile should return selected Trainee when id is valid")
    @Test
    void selectProfile_shouldReturnSelectedTraineeWhenIdIsValid() {
        Long validId = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(validId);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findById(validId)).thenReturn(Optional.of(trainee));

        Trainee selectedTrainee = traineeService.selectProfile(validId);

        assertNotNull(selectedTrainee);
        assertEquals(validId, selectedTrainee.getId());
        assertEquals("John", selectedTrainee.getFirstName());
        assertEquals("Doe", selectedTrainee.getLastName());

        verify(userValidator).validateId(validId);
        verify(traineeDao).findById(validId);

        verifyNoMoreInteractions(traineeDao, userValidator);
    }


    @DisplayName("selectProfile should throw IllegalArgumentException when id is invalid")
    @Test
    void selectProfile_shouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;

        doThrow(new IllegalArgumentException("ID must be a positive number"))
                .when(userValidator).validateId(invalidId);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.selectProfile(invalidId)
        );

        assertEquals("ID must be a positive number", ex.getMessage());

        verify(userValidator).validateId(invalidId);
        verifyNoInteractions(traineeDao);
    }

    @DisplayName("selectProfile should throw RuntimeException when Trainee not found")
    @Test
    void selectProfile_shouldThrowExceptionWhenTraineeNotFound() {
        Long traineeId = 2L;


        when(traineeDao.findById(traineeId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> traineeService.selectProfile(traineeId)
        );

        assertEquals("Trainee not found with id: " + traineeId, ex.getMessage());

        verify(userValidator).validateId(traineeId);
        verify(traineeDao).findById(traineeId);
        verifyNoMoreInteractions(traineeDao, userValidator);
    }


    @DisplayName("selectProfile should return selected Trainee when username is valid")
    @Test
        //Test happyPath method selectProfile by username
    void selectProfile_shouldReturnSelectedTraineeWhenUsernameIsValid() {
        String validUsername = "john.doe";
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername(validUsername);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findByUsername(validUsername)).thenReturn(Optional.of(trainee));

        Trainee selectedTrainee = traineeService.selectProfile(validUsername);

        assertNotNull(selectedTrainee);
        assertEquals(validUsername, selectedTrainee.getUsername());
        assertEquals("John", selectedTrainee.getFirstName());
        assertEquals("Doe", selectedTrainee.getLastName());

        verify(userValidator).validateUsername(validUsername);
        verify(traineeDao).findByUsername(validUsername);
        verifyNoMoreInteractions(traineeDao, userValidator);
    }


    //Test invalid username for selectProfile by username
    @DisplayName("selectProfile should throw IllegalArgumentException when username is invalid")
    @Test
    void selectProfile_shouldThrowExceptionWhenUsernameIsInvalid() {
        String invalidUsername = "   ";
        doThrow(new IllegalArgumentException("Username cannot be null or blank"))
                .when(userValidator).validateUsername(invalidUsername);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.selectProfile(invalidUsername)
        );

        assertEquals("Username cannot be null or blank", ex.getMessage());
        verify(userValidator).validateUsername(invalidUsername);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(traineeDao);
    }


    //Test trainee not found for selectProfile by username
    @DisplayName("selectProfile should throw RuntimeException when Trainee not found by username")
    @Test
    void selectProfile_shouldThrowExceptionWhenTraineeNotFoundByUsername() {
        String validUsername = "jane.doe";

        when(traineeDao.findByUsername(validUsername)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> traineeService.selectProfile(validUsername)
        );

        assertEquals("Trainee not found with username: " + validUsername, ex.getMessage());

        verify(userValidator).validateUsername(validUsername);
        verify(traineeDao).findByUsername(validUsername);
        verifyNoMoreInteractions(traineeDao, userValidator);
    }


    //Test getAllProfiles method should return all Trainee profiles work on map
    @DisplayName("getAllProfiles should return all Trainee profiles")
    @Test
    void getAllProfiles_shouldReturnAllTraineeProfiles() {
        Trainee trainee1 = new Trainee();
        trainee1.setId(1L);
        trainee1.setFirstName("John");
        trainee1.setLastName("Doe");

        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        trainee2.setFirstName("Jane");
        trainee2.setLastName("Smith");

        Map<Long, Trainee> traineeMap = Map.of(
                trainee1.getId(), trainee1,
                trainee2.getId(), trainee2
        );

        when(traineeDao.getAll()).thenReturn(traineeMap);

        Map<Long, Trainee> allTrainees = traineeService.getAllProfiles();

        assertNotNull(allTrainees);
        assertEquals(2, allTrainees.size());
        assertTrue(allTrainees.containsKey(1L));
        assertTrue(allTrainees.containsKey(2L));
        assertEquals("John", allTrainees.get(1L).getFirstName());
        assertEquals("Jane", allTrainees.get(2L).getFirstName());

        verify(traineeDao).getAll();
        verifyNoMoreInteractions(traineeDao);
        verifyNoInteractions(userValidator, credentialsGenerator);
    }


    //Test updateProfile happy path
    @DisplayName("updateProfile should return updated Trainee when inputs are valid")
    @Test
    void updateProfile_shouldReturnUpdatedTraineeWhenInputsAreValid() {
        Long validId = 1L;
        Trainee existingTrainee = new Trainee();
        existingTrainee.setId(validId);
        existingTrainee.setFirstName("John");
        existingTrainee.setLastName("Doe");
        existingTrainee.setActive(true);

        Trainee updatedInfo = new Trainee();
        updatedInfo.setFirstName("Jane");
        updatedInfo.setLastName("Smith");
        updatedInfo.setActive(false);

        when(traineeDao.findById(validId)).thenReturn(Optional.of(existingTrainee));
        when(traineeDao.update(eq(validId), existingTrainee));

        Trainee updatedTrainee = traineeService.updateProfile(validId, updatedInfo);

        assertNotNull(updatedTrainee);
        assertEquals(validId, updatedTrainee.getId());
        assertEquals("Jane", updatedTrainee.getFirstName());
        assertEquals("Smith", updatedTrainee.getLastName());
        assertFalse(updatedTrainee.isActive());

        verify(userValidator).validateId(validId);
        verify(userValidator).validateUser(updatedInfo);
        verify(traineeDao).findById(validId);
        verify(traineeDao).update(eq(validId), argThat(t ->
                t.getFirstName().equals("Jane") &&
                        t.getLastName().equals("Smith") &&
                        !t.isActive()
        ));
        verifyNoMoreInteractions(traineeDao, userValidator);

    }


    //Test updateProfile when id is invalid
    @DisplayName("updateProfile should throw IllegalArgumentException when id is invalid")
    @Test
    void updateProfile_shouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        doThrow(new IllegalArgumentException("ID must be a positive number"))
                .when(userValidator).validateId(invalidId);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.updateProfile(invalidId, trainee)
        );

        assertEquals("ID must be a positive number", ex.getMessage());

        verify(userValidator).validateId(invalidId);
        verifyNoInteractions(traineeDao);
    }

    //Test updateProfile when Trainee is invalid
    @DisplayName("updateProfile should throw IllegalArgumentException when Trainee is invalid")
    @Test
    void updateProfile_shouldThrowExceptionWhenTraineeIsInvalid() {
        Long validId = 1L;
        Trainee trainee = new Trainee();
        trainee.setFirstName("");
        trainee.setLastName("Doe");

        doThrow(new IllegalArgumentException("First name cannot be null or blank"))
                .when(userValidator).validateUser(trainee);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.updateProfile(validId, trainee)
        );

        assertEquals("First name cannot be null or blank", ex.getMessage());

        verify(userValidator).validateId(validId);
        verify(userValidator).validateUser(trainee);
        verifyNoInteractions(traineeDao);
    }


    //Test updateProfile when Trainee not found
    @DisplayName("updateProfile should throw RuntimeException when Trainee not found")
    @Test
    void updateProfile_shouldThrowExceptionWhenTraineeNotFound() {
        Long validId = 2L;
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findById(validId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> traineeService.updateProfile(validId, trainee)
        );

        assertEquals("Trainee not found with id: " + validId, ex.getMessage());

        verify(userValidator).validateId(validId);
        verify(userValidator).validateUser(trainee);
        verify(traineeDao).findById(validId);
        verifyNoMoreInteractions(traineeDao, userValidator);
    }


    //Test deleteProfile by id happy path
    @DisplayName("deleteProfile by id should delete Trainee when id is valid")
    @Test
    void deleteProfileById_shouldDeleteTraineeWhenIdIsValid() {
        Long validId = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(validId);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findById(validId)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteProfile(validId));

        verify(userValidator).validateId(validId);
        verify(traineeDao).findById(validId);
        verify(traineeDao).delete(validId);
        verifyNoMoreInteractions(traineeDao, userValidator);
    }

    //Test deleteProfile by id when id is invalid
    @DisplayName("deleteProfile by id should throw IllegalArgumentException when id is invalid")
    @Test
    void deleteProfileById_shouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;

        doThrow(new IllegalArgumentException("ID must be a positive number"))
                .when(userValidator).validateId(invalidId);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.deleteProfile(invalidId)
        );

        assertEquals("ID must be a positive number", ex.getMessage());

        verify(userValidator).validateId(invalidId);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(traineeDao);
    }


    //Test deleteProfile by id when Trainee not found
    @DisplayName("deleteProfile by id should throw RuntimeException when Trainee not found")
    @Test
    void deleteProfileById_shouldThrowExceptionWhenTraineeNotFound() {
        Long validId = 2L;

        when(traineeDao.findById(validId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> traineeService.deleteProfile(validId)
        );

        assertEquals("Trainee not found with id: " + validId, ex.getMessage());

        verify(userValidator).validateId(validId);
        verify(traineeDao).findById(validId);
        verifyNoMoreInteractions(traineeDao, userValidator);

    }

    //Test deleteProfile by username happy path
    @DisplayName("deleteProfile by username should delete Trainee when username is valid")
    @Test
    void deleteProfileByUsername_shouldDeleteTraineeWhenUsernameIsValid() {
        String validUsername = "john.doe";
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername(validUsername);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findByUsername(validUsername)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteProfile(validUsername));

        verify(userValidator).validateUsername(validUsername);
        verify(traineeDao).findByUsername(validUsername);
        verify(traineeDao).delete(trainee.getId());
        verifyNoMoreInteractions(traineeDao, userValidator);
    }

    //Test deleteProfile by username when username is invalid
    @DisplayName("deleteProfile by username should throw IllegalArgumentException when username is invalid")
    @Test
    void deleteProfileByUsername_shouldThrowExceptionWhenUsernameIsInvalid() {
        String invalidUsername = "   ";

        doThrow(new IllegalArgumentException("Username cannot be null or blank"))
                .when(userValidator).validateUsername(invalidUsername);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.deleteProfile(invalidUsername)
        );

        assertEquals("Username cannot be null or blank", ex.getMessage());

        verify(userValidator).validateUsername(invalidUsername);
        verifyNoMoreInteractions(userValidator);
        verifyNoInteractions(traineeDao);
    }

    //Test deleteProfile by username when Trainee not found
    @DisplayName("deleteProfile by username should throw RuntimeException when Trainee not found")
    @Test
    void deleteProfileByUsername_shouldThrowExceptionWhenTraineeNotFound() {
        String validUsername = "jane.doe";

        when(traineeDao.findByUsername(validUsername)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> traineeService.deleteProfile(validUsername)
        );

        assertEquals("Trainee not found with username: " + validUsername, ex.getMessage());

        verify(userValidator).validateUsername(validUsername);
        verify(traineeDao).findByUsername(validUsername);
        verifyNoMoreInteractions(traineeDao, userValidator);
    }
}
