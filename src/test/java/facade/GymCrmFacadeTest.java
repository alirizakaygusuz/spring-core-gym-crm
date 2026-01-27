package facade;

import com.alirizakaygusuz.gymcrm.exception.ValidationException;
import com.alirizakaygusuz.gymcrm.facade.GymCrmFacade;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.service.TraineeService;
import com.alirizakaygusuz.gymcrm.service.TrainerService;
import com.alirizakaygusuz.gymcrm.service.TrainingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymCrmFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymCrmFacade gymCrmFacade;


    @DisplayName("create Trainee profile should return created Trainee when called")
    @Test
    void createTraineeProfile_ShouldReturnCreatedTraineeWhenCalled() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        Trainee exceptedTrainee = new Trainee();
        exceptedTrainee.setFirstName("John");
        exceptedTrainee.setLastName("Doe");
        exceptedTrainee.setUsername("John.Doe");

        when(traineeService.createProfile(trainee)).thenReturn(exceptedTrainee);

        Trainee savedTrainee = gymCrmFacade.createTraineeProfile(trainee);

        assertSame(exceptedTrainee, savedTrainee);
        verify(traineeService).createProfile(trainee);
        verifyNoMoreInteractions(traineeService);
        verifyNoInteractions(trainerService, trainingService);
    }

    @DisplayName("update Trainee profile should return updated Trainee when called")
    @Test
    void updateTraineeProfile_ShouldReturnUpdatedTraineeWhenCalled() {
        Long traineeId = 1L;
        Trainee trainee = new Trainee();
        trainee.setFirstName("Jane");
        trainee.setLastName("Doe");

        Trainee exceptedTrainee = new Trainee();
        exceptedTrainee.setUsername("Jane.Doe");

        when(traineeService.updateProfile(traineeId, trainee)).thenReturn(exceptedTrainee);

        Trainee updatedTrainee = gymCrmFacade.updateTraineeProfile(traineeId, trainee);

        assertSame(exceptedTrainee, updatedTrainee);
        verify(traineeService).updateProfile(traineeId, trainee);
        verifyNoMoreInteractions(traineeService);
        verifyNoInteractions(trainerService, trainingService);
    }


    @DisplayName("delete Trainee profile should delete Trainee profile when called")
    @Test
    void deleteTraineeProfile_ShouldDeleteTraineeProfileWhenCalled() {
        Long traineeId = 1L;

        gymCrmFacade.deleteTraineeProfile(traineeId);

        verify(traineeService).deleteProfile(traineeId);
        verifyNoMoreInteractions(traineeService);
        verifyNoInteractions(trainerService, trainingService);

    }

    @DisplayName("select Trainee profile by id should return Trainee when called")
    @Test
    void selectTraineeProfileById_ShouldReturnTraineeWhenCalled() {
        Long traineeId = 1L;

        Trainee exceptedTrainee = new Trainee();
        exceptedTrainee.setId(traineeId);
        exceptedTrainee.setUsername("John.Doe");

        when(traineeService.selectProfile(traineeId)).thenReturn(exceptedTrainee);

        Trainee selectedTrainee = gymCrmFacade.selectTraineeProfileById(traineeId);

        assertSame(exceptedTrainee, selectedTrainee);
        verify(traineeService).selectProfile(traineeId);
        verifyNoMoreInteractions(traineeService);
        verifyNoInteractions(trainerService, trainingService);
    }


    @DisplayName("select Trainee profile by username should return Trainee when called")
    @Test
    void selectTraineeProfileByUsername_ShouldReturnTraineeWhenCalled() {
        String username = "John.Doe";

        Trainee exceptedTrainee = new Trainee();
        exceptedTrainee.setUsername(username);

        when(traineeService.selectProfile(username)).thenReturn(exceptedTrainee);

        Trainee selectedTrainee = gymCrmFacade.selectTraineeProfileByUsername(username);

        assertSame(exceptedTrainee, selectedTrainee);
        verify(traineeService).selectProfile(username);
        verifyNoMoreInteractions(traineeService);
        verifyNoInteractions(trainerService, trainingService);
    }

    @DisplayName("get all Trainee profiles should return all Trainee profiles when called")
    @Test
    void getAllTraineeProfiles_ShouldReturnAllTraineeProfilesWhenCalled() {
        gymCrmFacade.getAllTraineeProfiles();
        verify(traineeService).getAllProfiles();
        verifyNoMoreInteractions(traineeService);
        verifyNoInteractions(trainerService, trainingService);
    }


    @DisplayName("create Trainer profile should return created Trainer when called")
    @Test
    void createTrainerProfile_ShouldReturnCreatedTrainerWhenCalled() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        Trainer exceptedTrainer = new Trainer();
        exceptedTrainer.setFirstName("John");
        exceptedTrainer.setLastName("Doe");
        exceptedTrainer.setUsername("John.Doe");

        when(trainerService.createProfile(trainer)).thenReturn(exceptedTrainer);

        Trainer savedTrainer = gymCrmFacade.createTrainerProfile(trainer);

        assertSame(exceptedTrainer, savedTrainer);
        verify(trainerService).createProfile(trainer);
        verifyNoMoreInteractions(trainerService);
        verifyNoInteractions(traineeService, trainingService);
    }


    @DisplayName("select Trainer profile by id should return Trainer when called")
    @Test
    void selectTrainerProfileById_ShouldReturnTrainerWhenCalled() {
        Long trainerId = 1L;

        Trainer exceptedTrainer = new Trainer();
        exceptedTrainer.setId(trainerId);
        exceptedTrainer.setUsername("Jane.Doe");

        when(trainerService.selectProfile(trainerId)).thenReturn(exceptedTrainer);

        Trainer selectedTrainer = gymCrmFacade.selectTrainerProfileById(trainerId);

        assertSame(exceptedTrainer, selectedTrainer);
        verify(trainerService).selectProfile(trainerId);
        verifyNoMoreInteractions(trainerService);
        verifyNoInteractions(traineeService, trainingService);
    }


    @DisplayName("select Trainer profile by username should return Trainer when called")
    @Test
    void selectTrainerProfileByUsername_ShouldReturnTrainerWhenCalled() {
        String username = "John.Doe";

        Trainer exceptedTrainer = new Trainer();
        exceptedTrainer.setUsername(username);

        when(trainerService.selectProfile(username)).thenReturn(exceptedTrainer);

        Trainer selectedTrainer = gymCrmFacade.selectTrainerProfileByUsername(username);

        assertSame(exceptedTrainer, selectedTrainer);
        verify(trainerService).selectProfile(username);
        verifyNoMoreInteractions(trainerService);
        verifyNoInteractions(traineeService, trainingService);

    }

    @DisplayName("update Trainer profile should return updated Trainer when called")
    @Test
    void updateTrainerProfile_ShouldReturnUpdatedTrainerWhenCalled() {
        Long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Doe");

        Trainer exceptedTrainer = new Trainer();
        exceptedTrainer.setUsername("Jane.Doe");

        when(trainerService.updateProfile(trainerId, trainer)).thenReturn(exceptedTrainer);

        Trainer updatedTrainer = gymCrmFacade.updateTrainerProfile(trainerId, trainer);

        assertSame(exceptedTrainer, updatedTrainer);
        verify(trainerService).updateProfile(trainerId, trainer);
        verifyNoMoreInteractions(trainerService);
        verifyNoInteractions(traineeService, trainingService);

    }


    @DisplayName("get all Trainer profiles should return all Trainer profiles when called")
    @Test
    void getAllTrainerProfiles_ShouldReturnAllTrainerProfilesWhenCalled() {
        gymCrmFacade.getAllTrainerProfiles();
        verify(trainerService).getAllTrainers();
        verifyNoMoreInteractions(trainerService);
        verifyNoInteractions(traineeService, trainingService);
    }


    @DisplayName("create Training profile should return created Training when Training is valid")
    @Test
    void createTrainingProfile_ShouldReturnCreatedTrainingWhenTrainingIsValid() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainerId(2L);
        training.setTraineeId(3L);
        training.setTrainingName("Yoga Basics");

        when(traineeService.selectProfile(3L)).thenReturn(new Trainee());
        when(trainerService.selectProfile(2L)).thenReturn(new Trainer());

        Training expectedTraining = new Training();
        expectedTraining.setId(1L);
        expectedTraining.setTrainerId(2L);
        expectedTraining.setTraineeId(3L);
        expectedTraining.setTrainingName("Yoga Basics");

        when(trainingService.createProfile(training)).thenReturn(expectedTraining);
        Training createdTraining = gymCrmFacade.createTrainingProfile(training);

        assertSame(expectedTraining, createdTraining);
        verify(trainingService).createProfile(training);
        verify(traineeService).selectProfile(3L);
        verify(trainerService).selectProfile(2L);
        verifyNoMoreInteractions(trainingService,traineeService , trainerService);

    }


    @DisplayName("create Training profile should throw exception when Training is null")
    @Test
    void createTrainingProfile_ShouldThrowExceptionWhenTrainingIsNull() {
        Training nullTraining = null;

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> gymCrmFacade.createTrainingProfile(nullTraining)
        );

        assertEquals("Training object cannot be null", exception.getMessage());

        verifyNoInteractions(trainingService,traineeService, trainerService);
    }


    @DisplayName("select Training profile should return Training when called")
    @Test
    void selectTrainingProfile_ShouldReturnTrainingWhenCalled() {
        Long trainingId = 1L;

        Training expectedTraining = new Training();
        expectedTraining.setId(trainingId);
        expectedTraining.setTrainingName("Advanced Pilates");

        when(trainingService.selectProfile(trainingId)).thenReturn(expectedTraining);

        Training selectedTraining = gymCrmFacade.selectTrainingProfile(trainingId);

        assertSame(expectedTraining, selectedTraining);
        verify(trainingService).selectProfile(trainingId);
        verifyNoMoreInteractions(trainingService);
        verifyNoInteractions(traineeService, trainerService);

    }

    @DisplayName("get all Training profiles should return all Training profiles when called")
    @Test
    void getAllTrainingProfiles_ShouldReturnAllTrainingProfilesWhenCalled() {
        gymCrmFacade.getAllTrainingProfiles();
        verify(trainingService).getAllTrainings();
        verifyNoMoreInteractions(trainingService);
        verifyNoInteractions(traineeService, trainerService);
    }

}
