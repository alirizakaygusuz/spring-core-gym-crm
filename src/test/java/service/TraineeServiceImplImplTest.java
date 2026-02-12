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
import com.alirizakaygusuz.gymcrm.exception.AccessDeniedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.mapper.TraineeMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainingMapper;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.trainee.TraineeServiceImpl;
import com.alirizakaygusuz.gymcrm.service.user.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.SelfAccessValidator;
import com.alirizakaygusuz.gymcrm.service.validator.TrainingDateRangeValidator;
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
    private TrainingDateRangeValidator trainingDateRangeValidator;

    @Mock
    private SelfAccessValidator selfAccessValidator;

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
    @DisplayName("getProfile should return trainee profile when access is allowed")
    void getProfile_shouldReturnTraineeProfileWhenAccessIsAllowed() {
        String currentUsername = "John.Doe";
        String targetUsername = "John.Doe";

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

        when(traineeDao.findByUsernameWithDetails(targetUsername)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(response);

        TraineeProfileResponse result = traineeService.getProfile(currentUsername, targetUsername);

        assertNotNull(result);
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);
        verify(traineeDao).findByUsernameWithDetails(targetUsername);
        verify(traineeMapper).toProfileResponse(trainee);
        verifyNoMoreInteractions(selfAccessValidator, traineeDao, traineeMapper);
    }

    @Test
    @DisplayName("getProfile should throw AccessDeniedException when accessing another user's profile")
    void getProfile_shouldThrowAccessDeniedExceptionWhenAccessingAnotherUsersProfile() {
        String currentUsername = "John.Doe";
        String targetUsername = "Jane.Doe";

        doThrow(new AccessDeniedException("You can only access your own profile."))
                .when(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> traineeService.getProfile(currentUsername, targetUsername)
        );

        assertEquals("You can only access your own profile.", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);
        verifyNoMoreInteractions(selfAccessValidator);
        verifyNoInteractions(traineeDao, traineeMapper);
    }

    @Test
    @DisplayName("getProfile should throw ResourceNotFoundException when trainee not found")
    void getProfile_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        String currentUsername = "John.Doe";
        String targetUsername = "John.Doe";

        when(traineeDao.findByUsernameWithDetails(targetUsername)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.getProfile(currentUsername, targetUsername)
        );

        assertEquals("Trainee not found with username : 'John.Doe'", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);
        verify(traineeDao).findByUsernameWithDetails(targetUsername);
        verifyNoMoreInteractions(selfAccessValidator, traineeDao);
        verifyNoInteractions(traineeMapper);
    }

    @Test
    @DisplayName("updateProfile should update trainee and return response")
    void updateProfile_shouldUpdateTraineeAndReturnResponse() {
        String currentUsername = "John.Doe";
        String targetUsername = "John.Doe";
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

        when(traineeDao.findByUsernameWithDetails(targetUsername)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(updatedTrainee);
        when(traineeMapper.toProfileUpdateResponse(updatedTrainee)).thenReturn(response);

        TraineeProfileUpdateResponse result = traineeService.updateProfile(currentUsername, targetUsername, request);

        assertNotNull(result);
        assertEquals("John.Doe", result.username());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);
        verify(traineeDao).findByUsernameWithDetails(targetUsername);
        verify(userService).applyProfileUpdate(user, request);
        verify(traineeDao).update(trainee);
        verify(traineeMapper).toProfileUpdateResponse(updatedTrainee);
        verifyNoMoreInteractions(selfAccessValidator, traineeDao, userService, traineeMapper);
    }

    @Test
    @DisplayName("deleteProfile should delete trainee when access is allowed")
    void deleteProfile_shouldDeleteTraineeWhenAccessIsAllowed() {
        String currentUsername = "John.Doe";
        String targetUsername = "John.Doe";

        User user = new User();
        user.setUsername("John.Doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        when(traineeDao.findByUsername(targetUsername)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteProfile(currentUsername, targetUsername));

        verify(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);
        verify(traineeDao).findByUsername(targetUsername);
        verify(traineeDao).delete(trainee);
        verifyNoMoreInteractions(selfAccessValidator, traineeDao);
    }

    @Test
    @DisplayName("deleteProfile should throw ResourceNotFoundException when trainee not found")
    void deleteProfile_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        String currentUsername = "John.Doe";
        String targetUsername = "John.Doe";

        when(traineeDao.findByUsername(targetUsername)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.deleteProfile(currentUsername, targetUsername)
        );

        assertEquals("Trainee not found with username : 'John.Doe'", exception.getMessage());

        verify(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);
        verify(traineeDao).findByUsername(targetUsername);
        verifyNoMoreInteractions(selfAccessValidator, traineeDao);
    }

    @Test
    @DisplayName("setActiveStatus should update trainee active status")
    void setActiveStatus_shouldUpdateTraineeActiveStatus() {
        String currentUsername = "John.Doe";
        String targetUsername = "John.Doe";

        User user = new User();
        user.setUsername("John.Doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        when(traineeDao.findByUsername(targetUsername)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.setActiveStatus(currentUsername, targetUsername, true));

        verify(selfAccessValidator).assertSelfAccess(currentUsername, targetUsername);
        verify(traineeDao).findByUsername(targetUsername);
        verify(userService).setActiveStatus(user, true);
        verifyNoMoreInteractions(selfAccessValidator, traineeDao, userService);
    }
}