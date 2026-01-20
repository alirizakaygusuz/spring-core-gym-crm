package com.alirizakaygusuz.gymcrm.console;

import com.alirizakaygusuz.gymcrm.facade.GymCrmFacade;
import com.alirizakaygusuz.gymcrm.model.Trainee;
import com.alirizakaygusuz.gymcrm.model.Trainer;
import com.alirizakaygusuz.gymcrm.model.Training;
import com.alirizakaygusuz.gymcrm.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * ConsoleRunner is responsible for executing a series of test scenarios
 * to demonstrate the functionality of the Gym CRM application.
 *
 * <p>It uses the GymCrmFacade to perform operations related to
 * Trainee, Trainer, and Training profiles, including creation,
 * selection, updating, and deletion. The results of each operation
 * are printed to the console for verification.</p>
 */
@Component
public class ConsoleRunner {

    private GymCrmFacade facade;

    //Setter injection
    @Autowired
    public void setFacade(GymCrmFacade facade) {
        this.facade = facade;
    }

    public void run() {

        System.out.println("=============Hello Spring Core Gym Crm Task!==============");

        ScenarioState state = new ScenarioState();

        runTraineeFlow(state);
        runTrainerFlow(state);
        runTrainingFlow(state);

        printAllMaps();


        System.out.println("=== Console Test Finished ===");
    }

    // ===================== FLOWS =====================

    private void runTraineeFlow(ScenarioState state) {
        // ========Create Default Trainee=======
        Trainee trainee1 = new Trainee();
        trainee1.setFirstName("John");
        trainee1.setLastName("Smith");
        trainee1.setActive(true);
        trainee1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee1.setAddress("123 Main St, Cityville");

        Trainee trainee2 = new Trainee();
        trainee2.setFirstName("John");
        trainee2.setLastName("Smith");
        trainee2.setActive(true);
        trainee2.setDateOfBirth(LocalDate.of(1992, 2, 2));
        trainee2.setAddress("456 Elm St, Townsville");

        Trainee trainee3 = new Trainee();
        trainee3.setFirstName("John");
        trainee3.setLastName("Smith");
        trainee3.setActive(true);
        trainee3.setDateOfBirth(LocalDate.of(1988, 3, 3));
        trainee3.setAddress("789 Oak St, Villageville");

        // ========Create Trainee Profiles via GymCrmFacade=======
        state.createdTrainee1 = facade.createTraineeProfile(trainee1);
        state.createdTrainee2 = facade.createTraineeProfile(trainee2);
        state.createdTrainee3 = facade.createTraineeProfile(trainee3);

        System.out.println("==========[Trainee] Created========");
        System.out.println(" - trainee1: " + state.createdTrainee1.getUsername()
                + " (id=" + state.createdTrainee1.getId() + ", password:" + state.createdTrainee1.getPassword() + ")");
        System.out.println(" - trainee2: " + state.createdTrainee2.getUsername()
                + " (id=" + state.createdTrainee2.getId() + ", password:" + state.createdTrainee2.getPassword() + ")");
        System.out.println(" - trainee3: " + state.createdTrainee3.getUsername()
                + " (id=" + state.createdTrainee3.getId() + ", password:" + state.createdTrainee3.getPassword() + ")");
        System.out.println("Trainee Map Size:" + facade.getAllTraineeProfiles().size());

        // ========Select Trainee Profile=======
        System.out.println("==========[Trainee] Select========");

        // Select by Id
        System.out.println("Select By Id: " + state.createdTrainee1.getId());
        var selectedTraineeById = facade.selectTraineeProfileById(state.createdTrainee1.getId());
        System.out.println(" - Selected Trainee Profile: " + selectedTraineeById);

        // Select by Username
        System.out.println("Select By Username: " + state.createdTrainee2.getUsername());
        var selectedTraineeByUsername = facade.selectTraineeProfileByUsername(state.createdTrainee2.getUsername());
        System.out.println(" - Selected Trainee Profile: " + selectedTraineeByUsername);

        // ========Update Trainee Profile=======
        System.out.println("==========[Trainee] Update========");

        Trainee traineeUpdate = new Trainee();
        traineeUpdate.setFirstName("JohnUpdated");
        traineeUpdate.setLastName("SmithUpdated");
        traineeUpdate.setActive(false);
        traineeUpdate.setDateOfBirth(LocalDate.of(1991, 1, 1));
        traineeUpdate.setAddress("Updated Address, City");

        var updatedTrainee = facade.updateTraineeProfile(state.createdTrainee1.getId(), traineeUpdate);
        System.out.println(" - updated trainee1: " + updatedTrainee);

        // ========Delete Trainee Profile=======
        System.out.println("==========[Trainee] Delete========");

        facade.deleteTraineeProfile(state.createdTrainee3.getId());
        System.out.println(" - deleted trainee3 id=" + state.createdTrainee3.getId());
        System.out.println("Trainee Map Size After Delete:" + facade.getAllTraineeProfiles().size());
    }

    private void runTrainerFlow(ScenarioState state) {
        // ========Create Default Trainer=======
        Trainer trainer1 = new Trainer();
        trainer1.setFirstName("James");
        trainer1.setLastName("Taylor");
        trainer1.setActive(true);
        trainer1.setSpecialization(TrainingType.CARDIO);

        Trainer trainer2 = new Trainer();
        trainer2.setFirstName("James");
        trainer2.setLastName("Taylor");
        trainer2.setActive(true);
        trainer2.setSpecialization(TrainingType.STRENGTH);

        Trainer trainer3 = new Trainer();
        trainer3.setFirstName("James");
        trainer3.setLastName("Taylor");
        trainer3.setActive(true);
        trainer3.setSpecialization(TrainingType.FLEXIBILITY);

        // ========Create Trainer Profiles via GymCrmFacade=======
        state.createdTrainer1 = facade.createTrainerProfile(trainer1);
        state.createdTrainer2 = facade.createTrainerProfile(trainer2);
        state.createdTrainer3 = facade.createTrainerProfile(trainer3);

        System.out.println("==========[Trainer] Created========");
        System.out.println(" - trainer1: " + state.createdTrainer1.getUsername()
                + " (id=" + state.createdTrainer1.getId() + ", password:" + state.createdTrainer1.getPassword() + ")");
        System.out.println(" - trainer2: " + state.createdTrainer2.getUsername()
                + " (id=" + state.createdTrainer2.getId() + ", password:" + state.createdTrainer2.getPassword() + ")");
        System.out.println(" - trainer3: " + state.createdTrainer3.getUsername()
                + " (id=" + state.createdTrainer3.getId() + ", password:" + state.createdTrainer3.getPassword() + ")");
        System.out.println("Trainer Map Size:" + facade.getAllTrainerProfiles().size());

        // ========Select Trainer Profile=======
        System.out.println("==========[Trainer] Select========");

        // Select by Id
        System.out.println("Select By Id: " + state.createdTrainer1.getId());
        var selectedTrainerById = facade.selectTrainerProfileById(state.createdTrainer1.getId());
        System.out.println(" - Selected Trainer Profile: " + selectedTrainerById);

        // Select by Username
        System.out.println("Select By Username: " + state.createdTrainer2.getUsername());
        var selectedTrainerByUsername = facade.selectTrainerProfileByUsername(state.createdTrainer2.getUsername());
        System.out.println(" - Selected Trainer Profile: " + selectedTrainerByUsername);

        // ========Update Trainer Profile=======
        System.out.println("==========[Trainer] Update========");

        Trainer trainerUpdate = new Trainer();
        trainerUpdate.setFirstName("JamesUpdated");
        trainerUpdate.setLastName("TaylorUpdated");
        trainerUpdate.setActive(true);
        trainerUpdate.setSpecialization(TrainingType.STRENGTH);

        var updatedTrainer = facade.updateTrainerProfile(state.createdTrainer1.getId(), trainerUpdate);
        System.out.println(" - updated trainer1: " + updatedTrainer);
    }

    private void runTrainingFlow(ScenarioState state) {
        // ========Create Default Training=======
        Training training1 = new Training();
        training1.setTraineeId(state.createdTrainee1.getId());
        training1.setTrainerId(state.createdTrainer1.getId());
        training1.setTrainingName("Morning Cardio");
        training1.setTrainingType(TrainingType.CARDIO);
        training1.setTrainingDate(LocalDate.now());
        training1.setTrainingDuration(60);

        Training training2 = new Training();
        training2.setTraineeId(state.createdTrainee2.getId());
        training2.setTrainerId(state.createdTrainer2.getId());
        training2.setTrainingName("Evening Strength");
        training2.setTrainingType(TrainingType.STRENGTH);
        training2.setTrainingDate(LocalDate.now().plusDays(1));
        training2.setTrainingDuration(45);

        Training training3 = new Training();
        training3.setTraineeId(state.createdTrainee2.getId()); // trainee3 deleted, so don’t use it
        training3.setTrainerId(state.createdTrainer3.getId());
        training3.setTrainingName("Afternoon Flexibility");
        training3.setTrainingType(TrainingType.FLEXIBILITY);
        training3.setTrainingDate(LocalDate.now().plusDays(2));
        training3.setTrainingDuration(30);

        // ========Create Training Profiles via GymCrmFacade=======
        state.createdTraining1 = facade.createTrainingProfile(training1);
        state.createdTraining2 = facade.createTrainingProfile(training2);
        state.createdTraining3 = facade.createTrainingProfile(training3);

        System.out.println("==========[Training] Created========");
        System.out.println(" - training1: " + state.createdTraining1.getTrainingName()
                + " (id=" + state.createdTraining1.getId() + ")");
        System.out.println(" - training2: " + state.createdTraining2.getTrainingName()
                + " (id=" + state.createdTraining2.getId() + ")");
        System.out.println(" - training3: " + state.createdTraining3.getTrainingName()
                + " (id=" + state.createdTraining3.getId() + ")");
        System.out.println("Training Map Size:" + facade.getAllTrainingProfiles().size());

        // ========Select Training Profile=======
        System.out.println("==========[Training] Select========");

        System.out.println("Select By Id: " + state.createdTraining1.getId());
        var selectedTraining = facade.selectTrainingProfile(state.createdTraining1.getId());
        System.out.println(" - Selected Training Profile: " + selectedTraining);
    }

    private void printAllMaps() {
        // ========List ALL (print)=======
        System.out.println("==========[ALL] Print Maps========");

        System.out.println("--- Trainees ---");
        facade.getAllTraineeProfiles()
                .forEach((id, t) -> System.out.println("id=" + id + " -> " + t));

        System.out.println("--- Trainers ---");
        facade.getAllTrainerProfiles()
                .forEach((id, tr) -> System.out.println("id=" + id + " -> " + tr));

        System.out.println("--- Trainings ---");
        facade.getAllTrainingProfiles()
                .forEach((id, trn) -> System.out.println("id=" + id + " -> " + trn));
    }

    // ===================== STATE HOLDER =====================
    private class ScenarioState {
        private Trainee createdTrainee1;
        private Trainee createdTrainee2;
        private Trainee createdTrainee3;

        private Trainer createdTrainer1;
        private Trainer createdTrainer2;
        private Trainer createdTrainer3;

        private Training createdTraining1;
        private Training createdTraining2;
        private Training createdTraining3;
    }


}
