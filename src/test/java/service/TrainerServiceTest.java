package service;

import com.alirizakaygusuz.gymcrm.dao.TrainerDao;
import com.alirizakaygusuz.gymcrm.dao.TrainingTypeDao;
import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerCreateResponse;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.TrainerProfileResponse;
import com.alirizakaygusuz.gymcrm.exception.AuthenticationFailedException;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.mapper.TrainerMapper;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.TrainingType;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.TrainerService;
import com.alirizakaygusuz.gymcrm.service.UserService;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
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
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private UserService userService;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private CommonValidator commonValidator;

    @InjectMocks
    private TrainerService trainerService;

    @DisplayName("createProfile should return TrainerCreateResponse when request is valid and specialization exists")
    @Test
    void createProfile_shouldReturnCreateResponseWhenRequestIsValidAndSpecializationExists() {
        TrainerProfileRequest request = new TrainerProfileRequest("John", "Doe", true, 5L);

        TrainingType specialization = new TrainingType();
        specialization.setId(5L);

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("john.doe");

        Trainer savedTrainer = new Trainer();
        savedTrainer.setId(1L);
        savedTrainer.setUser(savedUser);
        savedTrainer.setSpecialization(specialization);

        TrainerCreateResponse response = mock(TrainerCreateResponse.class);

        when(trainingTypeDao.findById(5L)).thenReturn(Optional.of(specialization));
        when(userService.createUserWithCredentials(request)).thenReturn(savedUser);
        when(trainerDao.save(any(Trainer.class))).thenReturn(savedTrainer);
        when(trainerMapper.toCreateResponse(savedTrainer)).thenReturn(response);

        TrainerCreateResponse result = trainerService.createProfile(request);

        assertNotNull(result);
        assertSame(response, result);

        verify(commonValidator).validateNotNull(request, "Trainer profile creation request");
        verify(commonValidator).validateNotNull(5L, "Specialization ID");
        verify(trainingTypeDao).findById(5L);
        verify(userService).createUserWithCredentials(request);

        verify(trainerDao).save(argThat(t ->
                t.getUser() == savedUser &&
                        t.getSpecialization() == specialization
        ));

        verify(trainerMapper).toCreateResponse(savedTrainer);

        verifyNoMoreInteractions(commonValidator, trainingTypeDao, userService, trainerDao, trainerMapper);
    }

    @DisplayName("createProfile should throw ValidationException when request is null")
    @Test
    void createProfile_shouldThrowValidationExceptionWhenRequestIsNull() {
        TrainerProfileRequest request = null;

        doThrow(new ValidationException("Trainer profile creation request cannot be null"))
                .when(commonValidator).validateNotNull(request, "Trainer profile creation request");

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> trainerService.createProfile(request)
        );

        assertEquals("Trainer profile creation request cannot be null", ex.getMessage());

        verify(commonValidator).validateNotNull(null, "Trainer profile creation request");
        verifyNoMoreInteractions(commonValidator);
        verifyNoInteractions(trainingTypeDao, userService, trainerDao, trainerMapper);
    }

    @DisplayName("createProfile should throw ResourceNotFoundException when specialization not found")
    @Test
    void createProfile_shouldThrowResourceNotFoundExceptionWhenSpecializationNotFound() {
        TrainerProfileRequest request = new TrainerProfileRequest("John", "Doe", true, 5L);

        when(trainingTypeDao.findById(5L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.createProfile(request)
        );

        assertEquals("TrainingType not found with id : '5'", ex.getMessage());

        verify(commonValidator).validateNotNull(request, "Trainer profile creation request");
        verify(commonValidator).validateNotNull(5L, "Specialization ID");
        verify(trainingTypeDao).findById(5L);

        verifyNoMoreInteractions(commonValidator, trainingTypeDao);
        verifyNoInteractions(userService, trainerDao, trainerMapper);
    }

    @DisplayName("selectProfile should return TrainerProfileResponse when auth and trainer exist")
    @Test
    void selectProfile_shouldReturnProfileResponseWhenAuthAndTrainerExist() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(authUser);

        TrainerProfileResponse response = mock(TrainerProfileResponse.class);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(response);

        TrainerProfileResponse result = trainerService.selectProfile(login);

        assertNotNull(result);
        assertSame(response, result);

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");
        verify(trainerMapper).toProfileResponse(trainer);

        verifyNoMoreInteractions(userService, trainerDao, trainerMapper);
        verifyNoInteractions(trainingTypeDao, commonValidator);
    }

    @DisplayName("selectProfile should throw ResourceNotFoundException when trainer not found for authenticated user")
    @Test
    void selectProfile_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.selectProfile(login)
        );

        assertEquals("Trainer not found with username : 'john.doe'", ex.getMessage());

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");

        verifyNoMoreInteractions(userService, trainerDao);
        verifyNoInteractions(trainerMapper, trainingTypeDao, commonValidator);
    }

    @DisplayName("changePassword should call userService.changePassword when auth and trainer exist")
    @Test
    void changePassword_shouldCallUserServiceChangePasswordWhenValid() {
        LoginRequest login = mock(LoginRequest.class);
        String newPassword = "newPass";

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(authUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.changePassword(login, newPassword));

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");
        verify(userService).changePassword(authUser, newPassword);

        verifyNoMoreInteractions(userService, trainerDao);
        verifyNoInteractions(trainerMapper, trainingTypeDao, commonValidator);
    }

    @DisplayName("changePassword should throw ResourceNotFoundException when trainer not found")
    @Test
    void changePassword_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        LoginRequest login = mock(LoginRequest.class);
        String newPassword = "newPass";

        User authUser = new User();
        authUser.setUsername("john.doe");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.changePassword(login, newPassword)
        );

        assertEquals("Trainer not found with username : 'john.doe'", ex.getMessage());

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");

        verifyNoMoreInteractions(userService, trainerDao);
        verifyNoInteractions(trainerMapper, trainingTypeDao, commonValidator);
    }

    @DisplayName("activateTrainer should call userService.activate when auth and trainer exist")
    @Test
    void activateTrainer_shouldCallUserServiceActivateWhenValid() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(authUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.activateTrainer(login));

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");
        verify(userService).activate(authUser);

        verifyNoMoreInteractions(userService, trainerDao);
        verifyNoInteractions(trainerMapper, trainingTypeDao, commonValidator);
    }

    @DisplayName("deactivateTrainer should call userService.deactivate when auth and trainer exist")
    @Test
    void deactivateTrainer_shouldCallUserServiceDeactivateWhenValid() {
        LoginRequest login = mock(LoginRequest.class);

        User authUser = new User();
        authUser.setUsername("john.doe");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(authUser);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.deactivateTrainer(login));

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");
        verify(userService).deactivate(authUser);

        verifyNoMoreInteractions(userService, trainerDao);
        verifyNoInteractions(trainerMapper, trainingTypeDao, commonValidator);
    }

    @DisplayName("updateTrainerProfile should return TrainerProfileResponse and update specialization when specializationId is not null")
    @Test
    void updateTrainerProfile_shouldReturnProfileResponseAndUpdateSpecializationWhenSpecializationIdIsNotNull() {
        LoginRequest login = mock(LoginRequest.class);

        TrainerProfileRequest updateRequest = new TrainerProfileRequest("John", "Doe", true, 7L);

        User authUser = new User();
        authUser.setUsername("john.doe");

        User trainerUser = new User();
        trainerUser.setUsername("john.doe");

        TrainingType oldSpec = new TrainingType();
        oldSpec.setId(5L);

        TrainingType newSpec = new TrainingType();
        newSpec.setId(7L);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);
        trainer.setSpecialization(oldSpec);

        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setId(1L);
        updatedTrainer.setUser(trainerUser);
        updatedTrainer.setSpecialization(newSpec);

        TrainerProfileResponse response = mock(TrainerProfileResponse.class);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findById(7L)).thenReturn(Optional.of(newSpec));
        when(trainerDao.update(any(Trainer.class))).thenReturn(updatedTrainer);
        when(trainerMapper.toProfileResponse(updatedTrainer)).thenReturn(response);

        TrainerProfileResponse result = trainerService.updateTrainerProfile(login, updateRequest);

        assertNotNull(result);
        assertSame(response, result);
        assertSame(newSpec, trainer.getSpecialization());

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");
        verify(userService).applyProfileUpdate(trainerUser, updateRequest);

        verify(commonValidator).validateNotNull(7L, "Specialization ID");
        verify(trainingTypeDao).findById(7L);

        verify(trainerDao).update(argThat(t ->
                t.getId().equals(1L) &&
                        t.getUser() == trainerUser &&
                        t.getSpecialization() == newSpec
        ));

        verify(trainerMapper).toProfileResponse(updatedTrainer);

        verifyNoMoreInteractions(userService, trainerDao, trainingTypeDao, commonValidator, trainerMapper);
    }

    @DisplayName("updateTrainerProfile should return TrainerProfileResponse and not resolve specialization when specializationId is null")
    @Test
    void updateTrainerProfile_shouldReturnProfileResponseWhenSpecializationIdIsNull() {
        LoginRequest login = mock(LoginRequest.class);

        TrainerProfileRequest updateRequest = new TrainerProfileRequest("John", "Doe", true, null);

        User authUser = new User();
        authUser.setUsername("john.doe");

        User trainerUser = new User();
        trainerUser.setUsername("john.doe");

        TrainingType oldSpec = new TrainingType();
        oldSpec.setId(5L);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);
        trainer.setSpecialization(oldSpec);

        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setId(1L);
        updatedTrainer.setUser(trainerUser);
        updatedTrainer.setSpecialization(oldSpec);

        TrainerProfileResponse response = mock(TrainerProfileResponse.class);

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.of(trainer));
        when(trainerDao.update(any(Trainer.class))).thenReturn(updatedTrainer);
        when(trainerMapper.toProfileResponse(updatedTrainer)).thenReturn(response);

        TrainerProfileResponse result = trainerService.updateTrainerProfile(login, updateRequest);

        assertNotNull(result);
        assertSame(response, result);
        assertSame(oldSpec, trainer.getSpecialization());

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");
        verify(userService).applyProfileUpdate(trainerUser, updateRequest);
        verify(trainerDao).update(any(Trainer.class));
        verify(trainerMapper).toProfileResponse(updatedTrainer);

        verifyNoMoreInteractions(userService, trainerDao, trainerMapper);
        verifyNoInteractions(trainingTypeDao, commonValidator);
    }

    @DisplayName("updateTrainerProfile should throw ResourceNotFoundException when trainer not found")
    @Test
    void updateTrainerProfile_shouldThrowResourceNotFoundExceptionWhenTrainerNotFound() {
        LoginRequest login = mock(LoginRequest.class);
        TrainerProfileRequest updateRequest = new TrainerProfileRequest("John", "Doe", true, 7L);

        User authUser = new User();
        authUser.setUsername("john.doe");

        when(userService.authenticate(login)).thenReturn(authUser);
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.updateTrainerProfile(login, updateRequest)
        );

        assertEquals("Trainer not found with username : 'john.doe'", ex.getMessage());

        verify(userService).authenticate(login);
        verify(trainerDao).findByUsername("john.doe");

        verifyNoMoreInteractions(userService, trainerDao);
        verifyNoInteractions(trainingTypeDao, trainerMapper, commonValidator);
    }

    @DisplayName("selectProfile should throw AuthenticationFailedException when authentication fails")
    @Test
    void selectProfile_shouldThrowAuthenticationFailedExceptionWhenAuthenticationFails() {
        LoginRequest login = mock(LoginRequest.class);

        when(userService.authenticate(login))
                .thenThrow(new AuthenticationFailedException("Invalid username or password"));

        AuthenticationFailedException ex = assertThrows(
                AuthenticationFailedException.class,
                () -> trainerService.selectProfile(login)
        );

        assertEquals("Invalid username or password", ex.getMessage());

        verify(userService).authenticate(login);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(trainerDao, trainingTypeDao, trainerMapper, commonValidator);
    }
}
