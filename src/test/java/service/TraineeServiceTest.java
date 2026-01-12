package service;


import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.model.Trainee;
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

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        traineeService.setCredentialsGenerator(credentialsGenerator);
        traineeService.setUserValidator(userValidator);
    }

    @DisplayName("createProfile should return created Trainee when Trainee is valid")
    @Test
    void createProfile_shouldReturnCreatedTraineeWhenTraineeIsValid() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setActive(true);


        when(credentialsGenerator.generateUniqueUsername("John", "Smith"))
                .thenReturn("john.smith");
        when(credentialsGenerator.generateRandomPassword())
                .thenReturn("randomPassword123");


        trainee.setUsername("john.smith");
        trainee.setPassword("randomPassword123");

        when(traineeDao.save(trainee)).thenReturn(trainee);


        Trainee createdTrainee = traineeService.createProfile(trainee);

        assertNotNull(createdTrainee);
        assertEquals("john.smith", createdTrainee.getUsername());
        assertEquals("randomPassword123", createdTrainee.getPassword());

        verify(userValidator).validateUser(trainee);
        verify(credentialsGenerator).generateRandomPassword();
        verify(credentialsGenerator).generateUniqueUsername("John", "Smith");
        verify(traineeDao).save(argThat(savedTrainee ->
                savedTrainee.getUsername().equals("john.smith") &&
                        savedTrainee.getPassword().equals("randomPassword123")
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
    void selectProfile_shouldReturnSelectedTraineeWhenIdIsValid(){
        Long validId = 1L;
        trainee = new Trainee();
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
    void selectProfile_shouldThrowExceptionWhenIdIsInvalid(){
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
    void selectProfile_shouldThrowExceptionWhenTraineeNotFound(){
        Long validId = 2L;
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findById(validId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> traineeService.selectProfile(validId)
        );

        assertEquals("Trainee not found with id: " + validId, ex.getMessage());

        verify(userValidator).validateId(validId);
        verify(traineeDao).findById(validId);
        verifyNoMoreInteractions(traineeDao, userValidator);
    }


    @DisplayName("selectProfile should return selected Trainee when username is valid")
    @Test
    //Test happyPath method selectProfile by username
    void selectProfile_shouldReturnSelectedTraineeWhenUsernameIsValid() {
        String validUsername = "john.doe";
        trainee = new Trainee();
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


    //Test getAllProfiles method
    @DisplayName("getAllProfiles should return all Trainee profiles")
    @Test
    void getAllProfiles_shouldReturnAllTraineeProfiles() {
        traineeService.getAllProfiles();

        verify(traineeDao).getAll();
        verifyNoMoreInteractions(traineeDao);
    }


    //Test updateProfile happy path
    @DisplayName("updateProfile should return updated Trainee when Trainee is valid")
    @Test
    void updateProfile_shouldReturnUpdatedTraineeWhenTraineeIsValid() {
        Long validId = 1L;
        trainee = new Trainee();
        trainee.setId(validId);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.findById(validId)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(eq(validId), any(Trainee.class))).thenReturn(trainee);

        Trainee updatedTrainee = traineeService.updateProfile(validId, trainee);

        assertNotNull(updatedTrainee);
        assertEquals(validId, updatedTrainee.getId());
        assertEquals("John", updatedTrainee.getFirstName());
        assertEquals("Doe", updatedTrainee.getLastName());

        verify(userValidator).validateId(validId);
        verify(userValidator).validateUser(trainee);
        verify(traineeDao).findById(validId);
        verify(traineeDao).update(eq(validId), argThat(updated ->
                updated.getFirstName().equals("John") &&
                updated.getLastName().equals("Doe")
        ));
        verifyNoMoreInteractions(traineeDao, userValidator);
    }


    //Test updateProfile when id is invalid
    @DisplayName("updateProfile should throw IllegalArgumentException when id is invalid")
    @Test
    void updateProfile_shouldThrowExceptionWhenIdIsInvalid() {
        Long invalidId = -1L;
        trainee = new Trainee();
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
        trainee = new Trainee();
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
        trainee = new Trainee();
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
        trainee = new Trainee();
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
        trainee = new Trainee();
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
