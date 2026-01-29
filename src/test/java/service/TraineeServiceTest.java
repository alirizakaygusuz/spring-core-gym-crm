package service;

import com.alirizakaygusuz.gymcrm.dao.TraineeDao;
import com.alirizakaygusuz.gymcrm.dao.TraineeTrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeCreateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeProfileRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.TraineeProfileResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.mapper.TraineeMapper;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.model.*;
import com.alirizakaygusuz.gymcrm.service.TraineeService;
import com.alirizakaygusuz.gymcrm.service.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TraineeTrainerDao traineeTrainerDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserService userService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private CommonValidator commonValidator;

    @InjectMocks
    private TraineeService traineeService;

    @DisplayName("createProfile should return TraineeCreateResponse when request is valid")
    @Test
    void createProfile_shouldReturnCreateResponseWhenRequestIsValid() {
        TraineeProfileRequest request = new TraineeProfileRequest(
                "John",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "Address"
        );

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("john.doe");

        Trainee savedTrainee = new Trainee();
        savedTrainee.setId(1L);
        savedTrainee.setUser(savedUser);
        savedTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        savedTrainee.setAddress("Address");

        TraineeCreateResponse response = mock(TraineeCreateResponse.class);

        when(userService.createUserWithCredentials(request)).thenReturn(savedUser);
        when(traineeDao.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(traineeMapper.toCreateResponse(savedTrainee)).thenReturn(response);

        TraineeCreateResponse result = traineeService.createProfile(request);

        assertNotNull(result);
        assertSame(response, result);

        verify(userService).createUserWithCredentials(request);
        verify(traineeDao).save(argThat(t ->
                t.getUser() == savedUser &&
                        "Address".equals(t.getAddress()) &&
                        LocalDate.of(1990, 1, 1).equals(t.getDateOfBirth())
        ));
        verify(traineeMapper).toCreateResponse(savedTrainee);

        verifyNoMoreInteractions(userService, traineeDao, traineeMapper);
        verifyNoInteractions(traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("selectProfile should return TraineeProfileResponse when auth and trainee exist")
    @Test
    void selectProfile_shouldReturnProfileResponseWhenAuthAndTraineeExist() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(authUser);

        TraineeProfileResponse response = mock(TraineeProfileResponse.class);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(response);

        TraineeProfileResponse result = traineeService.selectProfile(login);

        assertNotNull(result);
        assertSame(response, result);

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(traineeMapper).toProfileResponse(trainee);

        verifyNoMoreInteractions(userService, traineeDao, traineeMapper);
        verifyNoInteractions(traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("selectProfile should throw ResourceNotFoundException when trainee not found for authenticated user")
    @Test
    void selectProfile_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.selectProfile(login)
        );

        assertEquals("Trainee not found with username : 'john.doe'", ex.getMessage());

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");

        verifyNoMoreInteractions(userService, traineeDao);
        verifyNoInteractions(traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("updateProfile should return TraineeProfileResponse when auth and trainee exist and update request is valid")
    @Test
    void updateProfile_shouldReturnProfileResponseWhenUpdateIsSuccessful() {
        LoginRequest login = mock(LoginRequest.class);

        TraineeProfileRequest updateRequest = new TraineeProfileRequest(
                "John",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "New Address"
        );

        User authUser = new User();
        authUser.setUsername("john.doe");

        User traineeUser = new User();
        traineeUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);
        trainee.setAddress("Old Address");

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setId(1L);
        updatedTrainee.setUser(traineeUser);
        updatedTrainee.setAddress("New Address");
        updatedTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));

        TraineeProfileResponse response = mock(TraineeProfileResponse.class);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeDao.update(any(Trainee.class))).thenReturn(updatedTrainee);
        when(traineeMapper.toProfileResponse(updatedTrainee)).thenReturn(response);

        TraineeProfileResponse result = traineeService.updateProfile(login, updateRequest);

        assertNotNull(result);
        assertSame(response, result);
        assertEquals("New Address", trainee.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(userService).applyProfileUpdate(traineeUser, updateRequest);
        verify(traineeDao).update(argThat(t ->
                t.getId().equals(1L) &&
                        "New Address".equals(t.getAddress()) &&
                        LocalDate.of(1990, 1, 1).equals(t.getDateOfBirth())
        ));
        verify(traineeMapper).toProfileResponse(updatedTrainee);

        verifyNoMoreInteractions(userService, traineeDao, traineeMapper);
        verifyNoInteractions(traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("updateProfile should throw ResourceNotFoundException when trainee not found for authenticated user")
    @Test
    void updateProfile_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        LoginRequest login = mock(LoginRequest.class);

        TraineeProfileRequest updateRequest = new TraineeProfileRequest(
                "John",
                "Doe",
                true,
                LocalDate.of(1990, 1, 1),
                "Address"
        );

        User authUser = new User();
        authUser.setUsername("john.doe");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.updateProfile(login, updateRequest)
        );

        assertEquals("Trainee not found with username : 'john.doe'", ex.getMessage());

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");

        verifyNoMoreInteractions(userService, traineeDao);
        verifyNoInteractions(traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("changePassword should call userService.changePassword when auth and trainee exist")
    @Test
    void changePassword_shouldCallUserServiceChangePasswordWhenValid() {
        LoginRequest login = mock(LoginRequest.class);
        String newPassword = "newPass";

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(authUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.changePassword(login, newPassword));

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(userService).changePassword(authUser, newPassword);

        verifyNoMoreInteractions(userService, traineeDao);
        verifyNoInteractions(traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("changePassword should throw ResourceNotFoundException when trainee not found")
    @Test
    void changePassword_shouldThrowResourceNotFoundExceptionWhenTraineeNotFound() {
        LoginRequest login = mock(LoginRequest.class);
        String newPassword = "newPass";

        User authUser = new User();
        authUser.setUsername("john.doe");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.changePassword(login, newPassword)
        );

        assertEquals("Trainee not found with username : 'john.doe'", ex.getMessage());

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");

        verifyNoMoreInteractions(userService, traineeDao);
        verifyNoInteractions(traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("activateTrainee should call userService.activate when auth and trainee exist")
    @Test
    void activateTrainee_shouldCallUserServiceActivateWhenValid() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setUser(authUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.activateTrainee(login));

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(userService).activate(authUser);

        verifyNoMoreInteractions(userService, traineeDao);
        verifyNoInteractions(traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("deactivateTrainee should call userService.deactivate when auth and trainee exist")
    @Test
    void deactivateTrainee_shouldCallUserServiceDeactivateWhenValid() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setUser(authUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deactivateTrainee(login));

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(userService).deactivate(authUser);

        verifyNoMoreInteractions(userService, traineeDao);
        verifyNoInteractions(traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("deleteTrainee should delete trainee when auth and trainee exist")
    @Test
    void deleteTrainee_shouldDeleteTraineeWhenValid() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        User traineeUser = new User();
        traineeUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteTrainee(login));

        verify(userService).authenticate(login);
        verify(traineeDao, times(2)).findByUsername("john.doe");
        verify(traineeDao).delete(trainee);

        verifyNoMoreInteractions(userService, traineeDao);
        verifyNoInteractions(traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }

    @DisplayName("getUnassignedTrainers should return mapped trainers when auth and trainee exist")
    @Test
    void getUnassignedTrainers_shouldReturnMappedTrainerProfilesWhenValid() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(authUser);

        Trainer trainer1 = new Trainer();
        trainer1.setId(100L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(200L);

        TrainerProfileResponse resp1 = mock(TrainerProfileResponse.class);
        TrainerProfileResponse resp2 = mock(TrainerProfileResponse.class);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeTrainerDao.findUnAssignedTrainersByTraineeUsername("john.doe"))
                .thenReturn(List.of(trainer1, trainer2));
        when(trainerMapper.toProfileResponse(trainer1)).thenReturn(resp1);
        when(trainerMapper.toProfileResponse(trainer2)).thenReturn(resp2);

        List<TrainerProfileResponse> result = traineeService.getUnassignedTrainers(login);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(resp1, result.get(0));
        assertSame(resp2, result.get(1));

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(traineeTrainerDao).findUnAssignedTrainersByTraineeUsername("john.doe");
        verify(trainerMapper).toProfileResponse(trainer1);
        verify(trainerMapper).toProfileResponse(trainer2);

        verifyNoMoreInteractions(userService, traineeDao, traineeTrainerDao, trainerMapper);
        verifyNoInteractions(traineeMapper, trainerDao, commonValidator);
    }

    @DisplayName("updateTraineeTrainers should delete old links and save new links when input is valid")
    @Test
    void updateTraineeTrainers_shouldDeleteOldLinksAndSaveNewLinksWhenValid() {
        LoginRequest login = mock(LoginRequest.class);
        List<Long> newTrainerIds = List.of(10L, 20L);

        User authUser = new User();
        authUser.setUsername("john.doe");

        User traineeUser = new User();
        traineeUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);

        Trainer trainer10 = new Trainer();
        trainer10.setId(10L);

        Trainer trainer20 = new Trainer();
        trainer20.setId(20L);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(10L)).thenReturn(Optional.of(trainer10));
        when(trainerDao.findById(20L)).thenReturn(Optional.of(trainer20));

        assertDoesNotThrow(() -> traineeService.updateTraineeTrainers(login, newTrainerIds));

        verify(userService).authenticate(login);
        verify(traineeDao,times(2)).findByUsername("john.doe");

        verify(commonValidator).validateNotNull(newTrainerIds, "New trainer IDs list cannot be null");
        verify(commonValidator).validateId(10L);
        verify(commonValidator).validateId(20L);

        verify(traineeTrainerDao).deleteAllByTraineeUsername("john.doe");
        verify(trainerDao).findById(10L);
        verify(trainerDao).findById(20L);

        verify(traineeTrainerDao, times(2)).save(argThat(link ->
                link.getTrainee() == trainee &&
                        link.getTrainer() != null &&
                        link.getId() != null &&
                        link.getId().getTraineeId().equals(1L) &&
                        (link.getId().getTrainerId().equals(10L) || link.getId().getTrainerId().equals(20L))
        ));

        verifyNoMoreInteractions(userService, traineeDao, traineeTrainerDao, trainerDao, commonValidator);
        verifyNoInteractions(traineeMapper, trainerMapper);
    }

    @DisplayName("updateTraineeTrainers should throw ValidationException when trainerIds list is null")
    @Test
    void updateTraineeTrainers_shouldThrowValidationExceptionWhenTrainerIdsIsNull() {
        LoginRequest login = mock(LoginRequest.class);
        List<Long> newTrainerIds = null;

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        doThrow(new ValidationException("New trainer IDs list cannot be null"))
                .when(commonValidator).validateNotNull(newTrainerIds, "New trainer IDs list cannot be null");

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> traineeService.updateTraineeTrainers(login, newTrainerIds)
        );

        assertEquals("New trainer IDs list cannot be null", ex.getMessage());

        verify(userService).authenticate(login);
        verify(traineeDao).findByUsername("john.doe");
        verify(commonValidator).validateNotNull(null, "New trainer IDs list cannot be null");

        verifyNoMoreInteractions(userService, traineeDao, commonValidator);
        verifyNoInteractions(traineeTrainerDao, trainerDao, traineeMapper, trainerMapper);
    }

    @DisplayName("updateTraineeTrainers should throw ResourceNotFoundException when trainer id not found")
    @Test
    void updateTraineeTrainers_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        LoginRequest login = mock(LoginRequest.class);
        List<Long> newTrainerIds = List.of(10L);

        User authUser = new User();
        authUser.setUsername("john.doe");

        User traineeUser = new User();
        traineeUser.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.updateTraineeTrainers(login, newTrainerIds)
        );

        assertEquals("Trainer not found with id : '10'", ex.getMessage());

        verify(userService).authenticate(login);
        verify(traineeDao,times(2)).findByUsername("john.doe");

        verify(commonValidator).validateNotNull(newTrainerIds, "New trainer IDs list cannot be null");
        verify(commonValidator).validateId(10L);

        verify(traineeTrainerDao).deleteAllByTraineeUsername("john.doe");
        verify(trainerDao).findById(10L);

        verifyNoMoreInteractions(userService, traineeDao, traineeTrainerDao, trainerDao, commonValidator);
        verifyNoInteractions(traineeMapper, trainerMapper);
    }

    @DisplayName("authenticateAndValidateTrainee flow should throw AuthenticationFailedException when authentication fails")
    @Test
    void selectProfile_shouldThrowAuthenticationFailedExceptionWhenAuthenticationFails() {
        LoginRequest login = mock(LoginRequest.class);

        when(userService.authenticate(login))
                .thenThrow(new AuthenticationFailedException("Invalid username or password"));

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> traineeService.selectProfile(login)
        );

        assertEquals("Invalid username or password", ex.getMessage());

        verify(userService).authenticate(login);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(traineeDao, traineeMapper, traineeTrainerDao, trainerDao, trainerMapper, commonValidator);
    }
}
