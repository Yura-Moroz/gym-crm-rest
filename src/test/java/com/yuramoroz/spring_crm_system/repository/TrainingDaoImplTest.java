package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.config.TestConfig;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class TrainingDaoImplTest {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    private Training training;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    public void setUp() {

        trainee = Trainee.builder()
                .firstName("Bob")
                .lastName("Santos")
                .userName("Bob.Santos")
                .password("qwerty")
                .active(true)
                .build();

        trainer = Trainer.builder()
                .firstName("TestTrainer")
                .lastName("TrainerTest")
                .userName("test.trainer")
                .password("qwerty")
                .active(true)
                .specialization("Java")
                .build();

        training = Training.builder()
                .trainingName("TestTraining")
                .trainingDuration(Duration.ofMinutes(60))
                .trainingDate(LocalDateTime.now())
                .trainingType(TrainingType.BACK_TRAINING)
                .trainee(trainee)
                .trainer(trainer)
                .build();

        traineeDao.save(trainee);
        trainerDao.save(trainer);
        trainingDao.save(training);
    }

    @Test
    public void saveTrainingTest() {
        Training resultTraining = trainingDao.save(training);

        assertNotNull(resultTraining.getId());
        assertEquals(resultTraining.getTrainingName(), training.getTrainingName());
        assertEquals(resultTraining.getTrainingType(), training.getTrainingType());
        assertEquals(resultTraining.getTrainingDuration(), training.getTrainingDuration());
        assertEquals(resultTraining.getTrainingDate(), training.getTrainingDate());
        assertEquals(resultTraining.getTrainee(), training.getTrainee());
        assertEquals(resultTraining.getTrainer(), training.getTrainer());
    }

    @Test
    public void getTrainingByIdTest() {

        Training newTraining = trainingDao.save(training);

        Optional<Training> resultTraining = trainingDao.getById(newTraining.getId());

        assertEquals(resultTraining.get(), newTraining);
    }

    @Test
    public void ifExistByIdTest() {

        Training newTraining = trainingDao.save(training);

        boolean exists = trainingDao.ifExistById(newTraining.getId());

        assertTrue(exists);
    }

    @Test
    public void getAllTrainingsTest() {

        Training training2 = Training.builder()
                .trainingName("TestTraining")
                .trainingDuration(Duration.ofMinutes(90))
                .trainingDate(LocalDateTime.now())
                .trainingType(TrainingType.FITNESS)
                .trainee(trainee)
                .trainer(trainer)
                .build();

        trainingDao.save(training);
        trainingDao.save(training2);

        List<Training> trainings = trainingDao.getAll();

        assertThat(trainings).contains(training, training2);
    }

    @Test
    public void getTrainingsByTraineeUsernameAndDateRangeTest() {

        List<Training> trainings = trainingDao.getTrainingsByTraineeUsernameAndDateRange(
                trainee.getUserName(), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(1), trainer.getUserName(), TrainingType.BACK_TRAINING);

        assertThat(trainings).containsExactlyInAnyOrder(training);
    }

    @Test
    public void getTrainingsByTrainerUsernameAndDateRangeTest() {

        List<Training> trainings = trainingDao.getTrainingsByTrainerUsernameAndDateRange(
                trainer.getUserName(), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(1), trainee.getUserName(), TrainingType.BACK_TRAINING);

        assertThat(trainings).containsExactlyInAnyOrder(training);
    }
}
