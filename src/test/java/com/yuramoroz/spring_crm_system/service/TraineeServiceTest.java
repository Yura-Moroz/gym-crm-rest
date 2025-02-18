package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.dto.trainees.TraineeDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.exceptionHandling.exceptions.NoSuchEntityPresentException;
import com.yuramoroz.spring_crm_system.profileHandlers.PasswordHandler;
import com.yuramoroz.spring_crm_system.repository.TraineeDao;
import com.yuramoroz.spring_crm_system.repository.TrainingDao;
import com.yuramoroz.spring_crm_system.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.convert.ConversionService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        trainee = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .address("123 Street")
                .userName("test.trainee")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void saveTraineeWithMultiParam_shouldSaveTraineeSuccessfully() {
        TraineeDto traineeDto = TraineeDto.builder()
                .id(1L)
                .firstName("Dwayne")
                .lastName("Johnson")
                .password("rockPass")
                .address("123 Street")
                .password("qwerrogpjhewr")
                .dateOfBirth(LocalDate.of(1972, 5, 2))
                .build();
        try (MockedStatic<PasswordHandler> mockedStatic = Mockito.mockStatic(PasswordHandler.class)) {
            mockedStatic.when(() -> PasswordHandler.verify(anyString())).thenReturn(true);
            when(traineeDao.ifExistByUsername(anyString())).thenReturn(false);
            when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);
            when(conversionService.convert(traineeDto, Trainee.class)).thenReturn(trainee);

            Trainee savedTrainee = traineeService.save(traineeDto);

            verify(traineeDao, times(1)).ifExistByUsername(anyString());
            verify(traineeDao, times(1)).save(any(Trainee.class));
        }
    }

    @Test
    void findByIdTest_withExistingTrainee() {
        when(traineeDao.getById(trainee.getId())).thenReturn(Optional.of(trainee));

        Trainee resultTrainee = traineeService.getById(trainee.getId()).get();

        verify(traineeDao, times(1)).getById(trainee.getId());
    }

    @Test
    void findByUsernameTest_ShouldReturnExistingUserByUsername() {
        when(traineeDao.getByUsername(trainee.getUserName())).thenReturn(Optional.of(trainee));

        Trainee resultTrainee = traineeService.getByUsername(trainee.getUserName()).get();

        verify(traineeDao, times(1)).getByUsername(trainee.getUserName());
    }

    @Test
    void deleteTraineeByUsernameTest() {
        when(traineeDao.ifExistByUsername(trainee.getUserName())).thenReturn(true);
        when(traineeDao.getByUsername(trainee.getUserName())).thenReturn(Optional.of(trainee));

        traineeService.deleteByUsername(trainee.getUserName());

        verify(traineeDao, times(1)).ifExistByUsername(trainee.getUserName());
        verify(traineeDao, times(1)).getByUsername(trainee.getUserName());
    }

    @Test
    void deleteTraineeByUsername_shouldLogWarningIfUserNotFound() {
        String username = "@nonexistent";

        when(traineeDao.ifExistByUsername(username)).thenReturn(false);

        traineeService.deleteByUsername(username);

        verify(traineeDao, never()).delete(any());
    }

    @Test
    void deleteTraineeByUserArgument() {
        traineeService.delete(trainee);

        verify(traineeDao, times(1)).delete(trainee);
    }

    @Test
    void updateTrainings_whenTraineeIsNull_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                traineeService.updateTrainings(null, Collections.emptyList())
        );
    }

    @Test
    void updateTrainings_whenTraineeNotFound_thenThrowNoSuchEntityPresentException() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeDao.ifExistById(1L)).thenReturn(false);

        assertThrows(NoSuchEntityPresentException.class, () ->
                traineeService.updateTrainings(trainee, Collections.emptyList())
        );
    }

    @Test
    void updateTrainings_whenTrainingDoesNotBelongToTrainee_thenThrowIllegalArgumentException() {

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeDao.ifExistById(1L)).thenReturn(true);

        // Create a training that belongs to a different trainee (id 2).
        Training training = new Training();
        training.setId(null); // new training, so no id assigned
        Trainee otherTrainee = new Trainee();
        otherTrainee.setId(2L);
        training.setTrainee(otherTrainee);

        List<Training> trainings = Arrays.asList(training);

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.updateTrainings(trainee, trainings)
        );
    }

    @Test
    void updateTrainings_updateExistingCreateNewDeleteOthers() {
        // --- SETUP ---
        long traineeId = 1L;
        Trainee persistentTrainee = new Trainee();
        persistentTrainee.setId(traineeId);

        // Existing trainings in the DB:
        // Training A with id 10 and Training B with id 20.
        Training trainingA = new Training();
        trainingA.setId(10L);
        trainingA.setTrainee(persistentTrainee);
        trainingA.setTrainingName("Old A");

        Training trainingB = new Training();
        trainingB.setId(20L);
        trainingB.setTrainee(persistentTrainee);
        trainingB.setTrainingName("Old B");

        persistentTrainee.setTrainings(new ArrayList<>(Arrays.asList(trainingA, trainingB)));

        when(traineeDao.ifExistById(traineeId)).thenReturn(true);
        when(traineeDao.getById(traineeId)).thenReturn(Optional.of(persistentTrainee));
        // When updating the trainee, just return the passed trainee.
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Prepare trainings for updating:
        // 1. For Training A: update it.
        Training updateA = new Training();
        updateA.setId(10L);
        updateA.setTrainee(persistentTrainee);
        updateA.setTrainingName("New A");
        updateA.setTrainer(new Trainer());
        updateA.setTrainingType(TrainingType.BOXES);
        updateA.setTrainingDate(LocalDateTime.now());
        updateA.setTrainingDuration(Duration.ofMinutes(60));

        // 2. New training (Training C) with no id.
        Training newTraining = new Training();
        newTraining.setId(null);
        newTraining.setTrainee(persistentTrainee);
        newTraining.setTrainingName("New C");
        newTraining.setTrainer(new Trainer());
        newTraining.setTrainingType(TrainingType.STRETCHING);
        newTraining.setTrainingDate(LocalDateTime.now());
        newTraining.setTrainingDuration(Duration.ofMinutes(90));

        List<Training> trainingsForUpdating = Arrays.asList(updateA, newTraining);

        // --- EXECUTE ---
        Trainee updatedTrainee = traineeService.updateTrainings(persistentTrainee, trainingsForUpdating);

        // ---VERIFY---
        assertEquals(2, updatedTrainee.getTrainings().size());

        // the updated Training A and the new training.
        List<Long> trainingIds = updatedTrainee.getTrainings().stream()
                .map(Training::getId)
                .collect(Collectors.toList());

        /**One is the updated training (with ID 10L).
        The other is the new training that was added (which might not have an ID yet if it’s generated later).*/
        assertTrue(trainingIds.contains(10L));

        // Check that the training names are as expected.
        List<String> trainingNames = updatedTrainee.getTrainings().stream()
                .map(Training::getTrainingName)
                .collect(Collectors.toList());
        assertTrue(trainingNames.contains("New A"));
        assertTrue(trainingNames.contains("New C"));
    }

    @Test
    void updateTraineeTest() {
        TraineeDto traineeDto = TraineeDto.builder()
                .id(1L)
                .firstName("Dwayne")
                .lastName("Johnson")
                .password("rockPass")
                .address("123 Street")
                .userName("test.trainee")
                .dateOfBirth(LocalDate.of(1972, 5, 2))
                .build();

        when(traineeDao.ifExistById(trainee.getId())).thenReturn(true);
        when(traineeDao.update(trainee)).thenReturn(trainee);
        when(conversionService.convert(traineeDto, Trainee.class)).thenReturn(trainee);
        when(traineeDao.getById(1L)).thenReturn(Optional.of(trainee));

        Trainee updatedTrainee = traineeService.update(1L, traineeDto);

        verify(traineeDao, times(1)).ifExistById(trainee.getId());
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void activateTraineeProfileTest() {
        when(traineeDao.update(any(Trainee.class))).thenReturn(trainee);

        trainee.setActive(false);

        boolean activated = traineeService.activate(trainee);

        assertTrue(activated);
        assertTrue(trainee.isActive());
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void deactivateTraineeProfileTest() {
        when(traineeDao.update(any(Trainee.class))).thenReturn(trainee);

        trainee.setActive(true);

        boolean deactivated = traineeService.deactivate(trainee);

        assertTrue(deactivated);
        assertFalse(trainee.isActive());
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void changeTraineeProfilePasswordTest() {
        String newPassword = "pass123";
        String oldPassword = trainee.getPassword();

        try (MockedStatic<PasswordHandler> mockedStatic = Mockito.mockStatic(PasswordHandler.class)) {
            mockedStatic.when(() -> PasswordHandler.verify(newPassword)).thenReturn(true);
            mockedStatic.when(() -> PasswordHandler.ifPasswordMatches(oldPassword, trainee.getPassword())).thenReturn(true);
            mockedStatic.when(() -> PasswordHandler.hashPassword(newPassword)).thenReturn(newPassword);
            when(traineeDao.ifExistById(trainee.getId())).thenReturn(true);
            when(traineeDao.update(trainee)).thenReturn(trainee);

            traineeService.changePassword(trainee, oldPassword, newPassword);

            assertEquals(trainee.getPassword(), newPassword);
            verify(traineeDao, times(1)).ifExistById(trainee.getId());
            verify(traineeDao, times(1)).update(trainee);
            mockedStatic.verify(() -> PasswordHandler.hashPassword(newPassword), times(1));
            mockedStatic.verify(() -> PasswordHandler.ifPasswordMatches(oldPassword, oldPassword), times(1));
            mockedStatic.verify(() -> PasswordHandler.verify(newPassword), times(1));
        }
    }
}
