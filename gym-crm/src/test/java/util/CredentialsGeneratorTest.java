package util;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialsGeneratorTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private CredentialsGenerator credentialsGenerator;

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

        when(traineeDao.findByUsername(expectedUsername)).thenReturn(Optional.empty());
        when(trainerDao.findByUsername(expectedUsername)).thenReturn(Optional.empty());

        String actualUsername = credentialsGenerator.generateUniqueUsername(firstName, lastName);

        assertEquals(expectedUsername, actualUsername);

        verify(traineeDao).findByUsername(expectedUsername);
        verify(trainerDao).findByUsername(expectedUsername);
        verifyNoMoreInteractions(traineeDao, trainerDao);
    }

    @DisplayName("generateUniqueUsername should return unique username when username is taken")
    @Test
    void generateUniqueUsername_shouldReturnUniqueUsernameWhenUsernameIsTaken() {
        String firstName = "Jane";
        String lastName = "Smith";
        String baseUsername = "Jane.Smith";
        String expectedUsername = "Jane.Smith2";

        when(traineeDao.findByUsername(baseUsername)).thenReturn(Optional.empty());
        when(trainerDao.findByUsername(baseUsername)).thenReturn(Optional.of(mock(Trainer.class)));

        when(traineeDao.findByUsername("Jane.Smith1")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("Jane.Smith1")).thenReturn(Optional.of(mock(Trainer.class)));

        when(traineeDao.findByUsername(expectedUsername)).thenReturn(Optional.empty());
        when(trainerDao.findByUsername(expectedUsername)).thenReturn(Optional.empty());

        String actualUsername = credentialsGenerator.generateUniqueUsername(firstName, lastName);

        assertEquals(expectedUsername, actualUsername);

        verify(traineeDao).findByUsername(baseUsername);
        verify(trainerDao).findByUsername(baseUsername);
        verify(traineeDao).findByUsername("Jane.Smith1");
        verify(trainerDao).findByUsername("Jane.Smith1");
        verify(traineeDao).findByUsername(expectedUsername);
        verify(trainerDao).findByUsername(expectedUsername);
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
