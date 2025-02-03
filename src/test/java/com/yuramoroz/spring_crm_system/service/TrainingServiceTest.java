package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.TrainingDao;
import com.yuramoroz.spring_crm_system.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);

        training = Training.builder()
                .id(1L)
                .trainingName("Calistenics")
                .trainingDate(LocalDateTime.now())
                .trainingType(TrainingType.CROSSFIT)
                .trainingDuration(Duration.ofMinutes(75))
                .build();
    }

    @Test
    public void saveTrainingTest(){
        trainingService.save(training);

        verify(trainingDao, times(1)).save(training);
    }

    @Test
    public void getTrainingByIdTest(){
        trainingService.getById(training.getId());

        verify(trainingDao, times(1)).getById(training.getId());
    }

    @Test
    public void getAllTrainings(){
        trainingService.getAll();

        verify(trainingDao, times(1)).getAll();
    }

    @Test
    public void getTrainingByTraineeUsernameAndDateRangeTest(){
        String traineeLogin = "Test.Trainee";
        String trainerLogin = "Test.Trainer";
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.of(2025, 6, 14);

        trainingService.getTrainingsByTraineeUsernameAndDateRange(
                traineeLogin, from, to, trainerLogin, TrainingType.ARMS_DAY);

        verify(trainingDao, times(1)).getTrainingsByTraineeUsernameAndDateRange(
                traineeLogin, from, to, trainerLogin, TrainingType.ARMS_DAY);
    }

    @Test
    public void getTrainingByTrainerUsernameAndDateRangeTest(){
        String trainerLogin = "Test.Trainer";
        String traineeLogin = "Test.Trainee";
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.of(2025, 6, 14);

        trainingService.getTrainingsByTrainerUsernameAndDateRange(
                trainerLogin, from, to, traineeLogin, TrainingType.ARMS_DAY);

        verify(trainingDao, times(1)).getTrainingsByTrainerUsernameAndDateRange(
                trainerLogin, from, to, traineeLogin, TrainingType.ARMS_DAY);
    }
}
