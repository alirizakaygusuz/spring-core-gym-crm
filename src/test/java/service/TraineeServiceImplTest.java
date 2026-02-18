package service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TraineeTrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingDao;
import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateResponse;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.mapper.TraineeMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainingMapper;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.trainee.TraineeServiceImpl;
import com.alirizakaygusuz.gymcrm.service.user.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TraineeTrainerDao traineeTrainerDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UserService userService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private ValidationUtils validationUtils;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("register should create trainee and return response")
    void register_shouldCreateTraineeAndReturnResponse() {
        TraineeRegisterRequest request = new TraineeRegisterRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St"
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("John.Doe");
        savedUser.setPassword("encodedPassword");

        Trainee savedTrainee = new Trainee();
        savedTrainee.setId(1L);
        savedTrainee.setUser(savedUser);

        TraineeRegisterResponse response = new TraineeRegisterResponse("John.Doe", "password");

        when(userService.createUserWithCredentials(request)).thenReturn(savedUser);
        when(traineeDao.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(traineeMapper.toRegisterResponse(savedUser)).thenReturn(response);

        TraineeRegisterResponse result = traineeService.register(request);

        assertNotNull(result);
        assertEquals("John.Doe", result.username());

        verify(userService).createUserWithCredentials(request);
        verify(traineeDao).save(any(Trainee.class));
        verify(traineeMapper).toRegisterResponse(savedUser);
        verifyNoMoreInteractions(userService, traineeDao, traineeMapper);
    }

    @Test
    @DisplayName("getProfile should return trainee profile when trainee exists")
    void getProfile_shouldReturnTraineeProfileWhenTraineeExists() {
        String username = "John.Doe";

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        TraineeProfileResponse response = new TraineeProfileResponse(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                true,
                null
        );

        when(traineeDao.findByUsernameWithDetails(username)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(response);

        TraineeProfileResponse result = traineeService.getProfile(username);

        assertNotNull(result);
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());

        verify(traineeDao).findByUsernameWithDetails(username);
        verify(traineeMapper).toProfileResponse(trainee);
        verifyNoMoreInteractions(traineeDao, traineeMapper);
    }

    @Test
    @DisplayName("getProfile should throw ResourceNotFoundException when trainee not found")
    void getProfile_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        String username = "John.Doe";

        when(traineeDao.findByUsernameWithDetails(username)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.getProfile(username)
        );

        assertTrue(exception.getMessage().contains("Trainee"));
        assertTrue(exception.getMessage().contains("username"));

        verify(traineeDao).findByUsernameWithDetails(username);
        verifyNoMoreInteractions(traineeDao);
        verifyNoInteractions(traineeMapper);
    }

    @Test
    @DisplayName("updateProfile should update trainee and return response")
    void updateProfile_shouldUpdateTraineeAndReturnResponse() {
        String username = "John.Doe";

        TraineeProfileUpdateRequest request = new TraineeProfileUpdateRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "456 New St",
                true
        );

        User user = new User();
        user.setUsername("John.Doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setId(1L);
        updatedTrainee.setUser(user);

        TraineeProfileUpdateResponse response = new TraineeProfileUpdateResponse(
                "John.Doe",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "456 New St",
                true,
                null
        );

        when(traineeDao.findByUsernameWithDetails(username)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(updatedTrainee);
        when(traineeMapper.toProfileUpdateResponse(updatedTrainee)).thenReturn(response);

        TraineeProfileUpdateResponse result = traineeService.updateProfile(username, request);

        assertNotNull(result);
        assertEquals("John.Doe", result.username());

        verify(traineeDao).findByUsernameWithDetails(username);
        verify(userService).applyProfileUpdate(user, request);
        verify(traineeDao).update(trainee);
        verify(traineeMapper).toProfileUpdateResponse(updatedTrainee);
        verifyNoMoreInteractions(traineeDao, userService, traineeMapper);
    }

    @Test
    @DisplayName("deleteProfile should delete trainee when trainee exists")
    void deleteProfile_shouldDeleteTraineeWhenTraineeExists() {
        String username = "John.Doe";

        User user = new User();
        user.setUsername("John.Doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteProfile(username));

        verify(traineeDao).findByUsername(username);
        verify(traineeDao).delete(trainee);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("deleteProfile should throw ResourceNotFoundException when trainee not found")
    void deleteProfile_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        String username = "John.Doe";

        when(traineeDao.findByUsername(username)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.deleteProfile(username)
        );

        assertTrue(exception.getMessage().contains("Trainee"));
        assertTrue(exception.getMessage().contains("username"));

        verify(traineeDao).findByUsername(username);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("setActiveStatus should update trainee active status")
    void setActiveStatus_shouldUpdateTraineeActiveStatus() {
        String username = "John.Doe";

        User user = new User();
        user.setUsername("John.Doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.setActiveStatus(username, true));

        verify(traineeDao).findByUsername(username);
        verify(userService).setActiveStatus(user, true);
        verifyNoMoreInteractions(traineeDao, userService);
    }
}
