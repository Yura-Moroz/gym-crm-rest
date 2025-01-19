package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.config.TestConfig;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.impl.TraineeDao;
import com.yuramoroz.spring_crm_system.repository.impl.TrainerDao;
import com.yuramoroz.spring_crm_system.repository.impl.TrainingDao;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(classes = TestConfig.class)
public class TraineeDaoTest {

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingDao trainingDao;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder()
                .firstName("Bob")
                .lastName("Santos")
                .userName("Bob.Santos")
                .password("qwerty")
                .active(true)
                .build();
    }

    @Test
    public void findByIdTest() {
        traineeDao.delete(trainee);
        traineeDao.save(trainee);

        Optional<Trainee> resultTrainee = traineeDao.getById(trainee.getId());

        assertEquals(resultTrainee.get(), trainee);

        traineeDao.delete(trainee);
        resultTrainee = traineeDao.getById(trainee.getId());

        assertEquals(Optional.empty(), resultTrainee);
    }

    @Test
    public void saveTraineeTest() {

        Trainee resultTrainee = traineeDao.save(trainee);

        assertNotNull(resultTrainee.getId());
        assertEquals(resultTrainee, trainee);
    }

    @Test
    public void updateTraineeTest() {
        String newPassword = "new_pass";
        String newUsername = "test.user";

        trainee = traineeDao.save(trainee);
        trainee.setPassword(newPassword);
        trainee.setUserName(newUsername);

        Trainee resultTrainee = traineeDao.update(trainee);

        assertEquals(resultTrainee.getPassword(), newPassword);
        assertEquals(resultTrainee.getUserName(), newUsername);
    }

    @Test
    public void deleteUserTest() {
        traineeDao.save(trainee);

        traineeDao.delete(trainee);

        assertFalse(traineeDao.ifExistById(trainee.getId()));
    }

    @Test
    public void getUserByUsernameTest_whenUsernameExists() {
        traineeDao.save(trainee);

        Optional<Trainee> resultTrainee = traineeDao.getUserByUsername(trainee.getUserName());

        assertEquals(trainee, resultTrainee.get());

    }

    @Test
    public void getUserByUsernameTest_whenThereIsNoSuchUsername() {
        assertThrows(NoSuchElementException.class, () -> traineeDao.getUserByUsername("!wrongUser!"));
    }

    @Test
    public void getUserByIdTest() {
        traineeDao.save(trainee);

        Optional<Trainee> resultTrainee = traineeDao.getById(trainee.getId());

        assertEquals(resultTrainee.get(), trainee);

        traineeDao.delete(trainee);

        resultTrainee = traineeDao.getById(trainee.getId());

        assertEquals(Optional.empty(), resultTrainee);
    }

    @Test
    public void ifExistByIdTest() {
        traineeDao.save(trainee);

        boolean exists = traineeDao.ifExistById(trainee.getId());

        assertTrue(exists);

        traineeDao.delete(trainee);

        exists = traineeDao.ifExistById(trainee.getId());

        assertFalse(exists);
    }

    @Test
    public void ifExistByUsernameTest() {
        traineeDao.save(trainee);

        String username = trainee.getUserName();
        boolean exists = traineeDao.ifUserExistByUsername(username);

        assertTrue(exists);

        traineeDao.delete(trainee);

        exists = traineeDao.ifUserExistByUsername(username);

        assertFalse(exists);
    }

    @Test
    public void getAllTraineesTest() {
        Trainee trainee2 = Trainee.builder()
                .firstName("Robert")
                .lastName("Martin")
                .userName("Uncle.Bob")
                .password("bobPass")
                .active(true)
                .build();

        traineeDao.save(trainee);
        traineeDao.save(trainee2);

        List<Trainee> trainees = traineeDao.getAll();

        assertTrue(trainees.size() > 1);
    }

    @Test
    public void getTrainingsByCriteriaTest() {
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

        List<Training> trainings = traineeDao.getTrainingsByCriteria(
                trainee.getUserName(), LocalDate.now(), LocalDate.of(2025, 1, 31), trainer.getFirstName(), TrainingType.BACK_TRAINING);

        assertTrue(trainings.size() > 0);
        assertTrue(trainings.contains(training));
    }
}
