package com.alirizakaygusuz.gymcrm.workload_service.service;


import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadSummaryResponse;
import com.alirizakaygusuz.gymcrm.workload_service.enums.ActionType;
import com.alirizakaygusuz.gymcrm.workload_service.exception.InsufficientTrainerWorkloadDurationException;
import com.alirizakaygusuz.gymcrm.workload_service.exception.TrainerWorkloadNotFoundException;
import com.alirizakaygusuz.gymcrm.workload_service.mapper.TrainerWorkloadMapper;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadMonthlySummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadSummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadYearlySummary;
import com.alirizakaygusuz.gymcrm.workload_service.monitoring.AppMetrics;
import com.alirizakaygusuz.gymcrm.workload_service.repository.TrainerWorkloadSummaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadSummaryRepository trainerWorkloadSummaryRepository;

    @Mock
    private TrainerWorkloadMapper trainerWorkloadMapper;

    @Mock
    private AppMetrics appMetrics;


    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;


    //Add  TrainerWorkloadSummary
    @Test
    @DisplayName("processTrainerWorkload should create new TrainerWorkloadSummary when trainer does not exist")
    void processTrainerWorkload_shouldCreateNewTrainerSummaryWhenTrainerDoesNotExist() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane",
                "Trainer",
                "Jane",
                true,
                LocalDate.of(2025, 3, 10),
                60,
                ActionType.ADD
        );

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verify(trainerWorkloadSummaryRepository).save(any(TrainerWorkloadSummary.class));
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);

    }

    @Test
    @DisplayName("processTrainerWorkload should accumulate training duration when trainer year and month already exist")
    void processTrainerWorkload_shouldAccumulateDurationTrainerSummaryWhenTrainerYearAndMonthAlreadyExist() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane",
                "Trainer",
                "Jane",
                true,
                LocalDate.of(2025, 3, 10),
                60,
                ActionType.ADD
        );


        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));


        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verify(trainerWorkloadSummaryRepository).save(any(TrainerWorkloadSummary.class));
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);

    }


    @Test
    @DisplayName("processTrainerWorkload should create new yearly summary when year does not exist")
    void processTrainerWorkload_shouldCreateNewYearlySummaryWhenYearDoesNotExist() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane",
                "Trainer",
                "Jane",
                true,
                LocalDate.of(2026, 3, 10),
                60,
                ActionType.ADD
        );


        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));


        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verify(trainerWorkloadSummaryRepository).save(any(TrainerWorkloadSummary.class));
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);

    }

    @Test
    @DisplayName("processTrainerWorkload should create new monthly summary when month does not exist in existing year")
    void processTrainerWorkload_shouldCreateNewMonthlySummaryWhenMonthDoesNotExistInExistingYear() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane",
                "Trainer",
                "Jane",
                true,
                LocalDate.of(2026, 12, 10),
                60,
                ActionType.ADD
        );


        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));


        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verify(trainerWorkloadSummaryRepository).save(any(TrainerWorkloadSummary.class));
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);

    }


    //Delete TrainerWorkloadSummary
    @Test
    @DisplayName("processTrainerWorkload with DELETE should process successfully when trainer year and month exist")
    void processTrainerWorkload_delete_shouldProcessSuccessfullyWhenTrainerYearAndMonthExist() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane", "Trainer", "Jane", true,
                LocalDate.of(2025, 3, 10), 60, ActionType.DELETE
        );

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verify(trainerWorkloadSummaryRepository).save(any(TrainerWorkloadSummary.class));
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
    }



    @Test
    @DisplayName("processTrainerWorkload with DELETE should reduce duration when remaining duration is greater than zero")
    void processTrainerWorkload_delete_shouldReduceDurationWhenRemainingDurationIsGreaterThanZero() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane", "Trainer", "Jane", true,
                LocalDate.of(2025, 3, 10), 30, ActionType.DELETE
        );

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(90);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertDoesNotThrow(() -> trainerWorkloadService.processTrainerWorkload(request));

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verify(trainerWorkloadSummaryRepository).save(any(TrainerWorkloadSummary.class));
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
    }



    @Test
    @DisplayName("processTrainerWorkload with DELETE should throw InsufficientTrainerWorkloadDurationException when duration goes below zero")
    void processTrainerWorkload_delete_shouldThrowInsufficientTrainerWorkloadDurationExceptionWhenDurationGoesBelowZero() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane", "Trainer", "Jane", true,
                LocalDate.of(2025, 3, 10), 90, ActionType.DELETE
        );

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertThrows(
                InsufficientTrainerWorkloadDurationException.class,
                () -> trainerWorkloadService.processTrainerWorkload(request)
        );

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
    }





    @Test
    @DisplayName("processTrainerWorkload with DELETE should throw TrainerWorkloadNotFoundException when trainer does not exist")
    void processTrainerWorkload_delete_shouldThrowTrainerWorkloadNotFoundExceptionWhenTrainerDoesNotExist() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane", "Trainer", "Jane", true,
                LocalDate.of(2025, 3, 10), 60, ActionType.DELETE
        );

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.processTrainerWorkload(request)
        );

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
    }


    @Test
    @DisplayName("processTrainerWorkload with DELETE should throw TrainerWorkloadNotFoundException when yearly summary does not exist")
    void processTrainerWorkload_delete_shouldThrowTrainerWorkloadNotFoundExceptionWhenYearlySummaryDoesNotExist() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane", "Trainer", "Jane", true,
                LocalDate.of(2025, 3, 10), 60, ActionType.DELETE
        );

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2024);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.processTrainerWorkload(request)
        );

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
    }


    @Test
    @DisplayName("processTrainerWorkload with DELETE should throw TrainerWorkloadNotFoundException when monthly summary does not exist")
    void processTrainerWorkload_delete_shouldThrowTrainerWorkloadNotFoundExceptionWhenMonthlySummaryDoesNotExist() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.jane", "Trainer", "Jane", true,
                LocalDate.of(2025, 3, 10), 60, ActionType.DELETE
        );

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(1);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername("trainer.jane");
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries("trainer.jane"))
                .thenReturn(Optional.of(existingSummary));

        assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.processTrainerWorkload(request)
        );

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries("trainer.jane");
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
    }







    //Get TrainerWorkloadSummary
    @Test
    @DisplayName("getTrainerWorkloadSummary should return response when trainer year and month exist")
    void getTrainerWorkloadSummary_shouldReturnResponseWhenTrainerYearAndMonthExist() {
        String username = "trainer.jane";

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername(username);
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        TrainerWorkloadSummaryResponse response = new TrainerWorkloadSummaryResponse(
                username, "Trainer", "Jane", true, null
        );

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries(username))
                .thenReturn(Optional.of(existingSummary));
        when(trainerWorkloadMapper.toTrainerWorkloadSummaryResponse(existingSummary))
                .thenReturn(response);

        TrainerWorkloadSummaryResponse result = trainerWorkloadService.getTrainerWorkloadSummary(username, 2025, 3);

        assertNotNull(result);
        assertEquals(username, result.username());

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries(username);
        verify(trainerWorkloadMapper).toTrainerWorkloadSummaryResponse(existingSummary);
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository, trainerWorkloadMapper);
    }

    @Test
    @DisplayName("getTrainerWorkloadSummary should throw TrainerWorkloadNotFoundException when trainer does not exist")
    void getTrainerWorkloadSummary_shouldThrowTrainerWorkloadNotFoundExceptionWhenTrainerDoesNotExist() {
        String username = "trainer.jane";

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries(username))
                .thenReturn(Optional.empty());

        TrainerWorkloadNotFoundException exception = assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.getTrainerWorkloadSummary(username, 2025, 3)
        );

        assertTrue(exception.getMessage().contains(username));

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries(username);
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
        verifyNoInteractions(trainerWorkloadMapper);
    }



    @Test
    @DisplayName("getTrainerWorkloadSummary should throw TrainerWorkloadNotFoundException when yearly summary does not exist")
    void getTrainerWorkloadSummary_shouldThrowTrainerWorkloadNotFoundExceptionWhenYearlySummaryDoesNotExist() {
        String username = "trainer.jane";

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername(username);
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries(username))
                .thenReturn(Optional.of(existingSummary));

        assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.getTrainerWorkloadSummary(username, 2026, 3)
        );


        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries(username);
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
        verifyNoInteractions(trainerWorkloadMapper);
    }


    @Test
    @DisplayName("getTrainerWorkloadSummary should throw TrainerWorkloadNotFoundException when monthly summary does not exist")
    void getTrainerWorkloadSummary_shouldThrowTrainerWorkloadNotFoundExceptionWhenMonthlySummaryDoesNotExist() {
        String username = "trainer.jane";

        TrainerWorkloadMonthlySummary existingMonthlySummary = new TrainerWorkloadMonthlySummary();
        existingMonthlySummary.setMonth(3);
        existingMonthlySummary.setTotalTrainingDuration(60);

        TrainerWorkloadYearlySummary existingYearlySummary = new TrainerWorkloadYearlySummary();
        existingYearlySummary.setYear(2025);
        existingYearlySummary.setMonthlySummaries(new ArrayList<>(List.of(existingMonthlySummary)));

        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary();
        existingSummary.setUsername(username);
        existingSummary.setYearlySummaries(new ArrayList<>(List.of(existingYearlySummary)));

        when(trainerWorkloadSummaryRepository.findByUsernameWithSummaries(username))
                .thenReturn(Optional.of(existingSummary));

       assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.getTrainerWorkloadSummary(username, 2025, 12)
        );

        verify(trainerWorkloadSummaryRepository).findByUsernameWithSummaries(username);
        verifyNoMoreInteractions(trainerWorkloadSummaryRepository);
        verifyNoInteractions(trainerWorkloadMapper);
    }



}



