package util;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialsGeneratorTest {

    @Mock
    TraineeDao traineeDao;

    @Mock
    TrainerDao trainerDao;

    @InjectMocks
    CredentialsGenerator credentialsGenerator;

    @BeforeEach
    void setUp() {
        credentialsGenerator.setTraineeDao(traineeDao);
        credentialsGenerator.setTrainerDao(trainerDao);
    }



    @DisplayName("generateUniqueUsername should return unique username when username is free")
    @Test
    void generateUniqueUsername_shouldReturnUniqueUsernameWhenUsernameIsFree() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUsername = "John.Doe";

        when(traineeDao.existsByUsername(expectedUsername)).thenReturn(false);
        when(trainerDao.existsByUsername(expectedUsername)).thenReturn(false);

        String actualUsername = credentialsGenerator.generateUniqueUsername(firstName, lastName);

        assertEquals(expectedUsername, actualUsername);

        verify(traineeDao).existsByUsername(expectedUsername);
        verify(trainerDao).existsByUsername(expectedUsername);
        verifyNoMoreInteractions(traineeDao, trainerDao);

    }

    @DisplayName("generateUniqueUsername should return unique username when username is taken")
    @Test
    void generateUniqueUsername_shouldReturnUniqueUsernameWhenUsernameIsTaken() {
        String firstName = "Jane";
        String lastName = "Smith";
        String baseUsername = "Jane.Smith";
        String expectedUsername = "Jane.Smith2";

        when(traineeDao.existsByUsername(baseUsername)).thenReturn(false);
        when(trainerDao.existsByUsername(baseUsername)).thenReturn(true);
        when(traineeDao.existsByUsername("Jane.Smith1")).thenReturn(false);
        when(trainerDao.existsByUsername("Jane.Smith1")).thenReturn(true);
        when(traineeDao.existsByUsername(expectedUsername)).thenReturn(false);
        when(trainerDao.existsByUsername(expectedUsername)).thenReturn(false);

        String actualUsername = credentialsGenerator.generateUniqueUsername(firstName, lastName);

        assertEquals(expectedUsername, actualUsername);

        verify(traineeDao).existsByUsername(baseUsername);
        verify(trainerDao).existsByUsername(baseUsername);
        verify(traineeDao).existsByUsername("Jane.Smith1");
        verify(trainerDao).existsByUsername("Jane.Smith1");
        verify(traineeDao).existsByUsername(expectedUsername);
        verify(trainerDao).existsByUsername(expectedUsername);
        verifyNoMoreInteractions(traineeDao, trainerDao);
    }


    @DisplayName("generateRandomPassword should generate a random password of correct length and characters")
    @Test
    void generateRandomPassword_shouldGeneratePasswordWhenMethodCalled() {
        String password = credentialsGenerator.generateRandomPassword();

        assertNotNull(password);
        assertTrue(password.matches("[A-Za-z0-9]{10}"));
    }

}
