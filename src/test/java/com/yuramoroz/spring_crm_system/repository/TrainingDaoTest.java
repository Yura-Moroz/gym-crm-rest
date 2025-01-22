package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.config.TestConfig;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class TrainingDaoTest {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Test
    public void getTrainingsByTraineeUsernameAndDateRangeTest() {
        Trainee trainee = Trainee.builder()
                .firstName("Bob")
                .lastName("Santos")
                .userName("Bob.Santos")
                .password("qwerty")
                .active(true)
                .build();

        Trainer trainer = Trainer.builder()
                .firstName("TestTrainer")
                .lastName("TrainerTest")
                .userName("test.trainer")
                .password("qwerty")
                .active(true)
                .specialization("Java")
                .build();

        Training training = Training.builder()
                .trainingName("TestTraining")
                .trainingDuration(Duration.ofMinutes(60))
                .trainingDate(LocalDateTime.of(2025, 1, 22, 13, 0))
                .trainingType(TrainingType.BACK_TRAINING)
                .trainee(trainee)
                .trainer(trainer)
                .build();

        trainerDao.save(trainer);
        traineeDao.save(trainee);
        trainingDao.save(training);

        List<Training> trainings = trainingDao.getTrainingsByTraineeUsernameAndDateRange(
                trainee.getUserName(), LocalDate.now(), LocalDate.of(2025, 1, 31), trainer.getFirstName(), TrainingType.BACK_TRAINING);

        assertTrue(trainings.size() > 0);
        assertTrue(trainings.contains(training));
    }
}
