package service;

import com.alirizakaygusuz.gymcrm.dao.UserDao;
import com.alirizakaygusuz.gymcrm.dto.common.UserCreationResult;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.model.RoleType;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.service.user.UserDomainService;
import com.alirizakaygusuz.gymcrm.service.user.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("createUserWithCredentials should save and return user")
    void createUserWithCredentials_shouldSaveAndReturnUser() {
        TraineeRegisterRequest request = new TraineeRegisterRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St"
        );

        User builtUser = new User();
        builtUser.setFirstName("John");
        builtUser.setLastName("Doe");
        builtUser.setUsername("John.Doe");
        builtUser.setActive(true);

        UserCreationResult creationResult = new UserCreationResult(builtUser, "rawPassword");


        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setUsername("John.Doe");
        savedUser.setActive(true);

        when(userDomainService.createWithCredentials(request, RoleType.TRAINER)).thenReturn(creationResult);
        when(userDao.save(builtUser)).thenReturn(savedUser);

        UserCreationResult result = userService.createUserWithCredentials(request , RoleType.TRAINER);

        assertNotNull(result);
        assertEquals(1L, result.user().getId());
        assertEquals("John", result.user().getFirstName());
        assertEquals("Doe", result.user().getLastName());
        assertEquals("John.Doe", result.user().getUsername());
        assertTrue(result.user().isActive());

        verify(userDomainService).createWithCredentials(request,RoleType.TRAINER);
        verify(userDao).save(builtUser);
        verifyNoMoreInteractions(userDomainService, userDao);
    }

    @Test
    @DisplayName("changePassword should update user password")
    void changePassword_shouldUpdateUserPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");
        user.setPassword("oldPassword");

        String newPassword = "newPassword123";

        assertDoesNotThrow(() -> userService.changePassword(user, newPassword));

        verify(userDomainService).changePassword(user, newPassword);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
    }

    @Test
    @DisplayName("setActiveStatus should update user active status to true")
    void setActiveStatus_shouldUpdateUserActiveStatusToTrue() {
        User user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");
        user.setActive(false);

        assertDoesNotThrow(() -> userService.setActiveStatus(user, true));

        verify(userDomainService).setActiveStatus(user, true);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
    }

    @Test
    @DisplayName("setActiveStatus should update user active status to false")
    void setActiveStatus_shouldUpdateUserActiveStatusToFalse() {
        User user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");
        user.setActive(true);

        assertDoesNotThrow(() -> userService.setActiveStatus(user, false));

        verify(userDomainService).setActiveStatus(user, false);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
    }

    @Test
    @DisplayName("setActiveStatus should throw ValidationException when user already in desired state")
    void setActiveStatus_shouldThrowValidationExceptionWhenUserAlreadyInDesiredState() {
        User user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");
        user.setActive(true);

        doThrow(new ValidationException("User is already active"))
                .when(userDomainService).setActiveStatus(user, true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.setActiveStatus(user, true)
        );

        assertEquals("User is already active", exception.getMessage());

        verify(userDomainService).setActiveStatus(user, true);
        verifyNoMoreInteractions(userDomainService);
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("applyProfileUpdate should update user profile")
    void applyProfileUpdate_shouldUpdateUserProfile() {
        User user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");

        TraineeProfileUpdateRequest request = new TraineeProfileUpdateRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                true
        );

        assertDoesNotThrow(() -> userService.applyProfileUpdate(user, request));

        verify(userDomainService).applyUpdate(user, request);
        verify(userDao).update(user);
        verifyNoMoreInteractions(userDomainService, userDao);
    }

    @Test
    @DisplayName("applyProfileUpdate should throw ValidationException when request is invalid")
    void applyProfileUpdate_shouldThrowValidationExceptionWhenRequestIsInvalid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");

        TraineeProfileUpdateRequest request = new TraineeProfileUpdateRequest(
                null,
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                true
        );

        doThrow(new ValidationException("First name cannot be null or blank"))
                .when(userDomainService).applyUpdate(user, request);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.applyProfileUpdate(user, request)
        );

        assertEquals("First name cannot be null or blank", exception.getMessage());

        verify(userDomainService).applyUpdate(user, request);
        verifyNoMoreInteractions(userDomainService);
        verifyNoInteractions(userDao);
    }
}