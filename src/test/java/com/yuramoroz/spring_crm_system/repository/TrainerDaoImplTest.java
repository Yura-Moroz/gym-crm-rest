package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class TrainerDaoImplTest {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainingDao trainingDao;

    private Trainer trainer;

    @BeforeEach
    public void setUp() {
        trainer = Trainer.builder()
                .firstName("Connor")
                .lastName("Mcgregor")
                .userName("Connor.Mcgregor")
                .password("qwerty123")
                .active(true)
                .specialization("MMA")
                .build();
    }

    @Test
    public void saveTrainerTest() {
        Trainer resultTrainer = trainerDao.save(trainer);

        assertNotNull(resultTrainer.getId());
        assertEquals(resultTrainer.getFirstName(), trainer.getFirstName());
        assertEquals(resultTrainer.getLastName(), trainer.getLastName());
        assertEquals(resultTrainer.getUserName(), trainer.getUserName());
        assertEquals(resultTrainer.getPassword(), trainer.getPassword());
        assertEquals(resultTrainer.isActive(), trainer.isActive());
        assertEquals(resultTrainer.getSpecialization(), trainer.getSpecialization());
    }

    @Test
    public void updateTrainerTest() {
        String newPassword = "new_pass";
        String newUsername = "test.user";

        Trainer newTrainer = trainerDao.save(trainer);
        newTrainer.setPassword(newPassword);
        newTrainer.setUserName(newUsername);

        Trainer updatedTrainer = trainerDao.update(newTrainer);

        assertEquals(updatedTrainer.getPassword(), newPassword);
        assertEquals(updatedTrainer.getUserName(), newUsername);
    }

    @Test
    public void deleteUserTest() {
        Trainer newTrainer = trainerDao.save(trainer);

        trainerDao.delete(newTrainer);

        assertFalse(trainerDao.ifExistById(newTrainer.getId()));
    }

    @Test
    public void getUserByUsernameTest_whenUsernameExists() {
        Trainer newTrainer = trainerDao.save(trainer);

        Optional<Trainer> resultTrainer = trainerDao.getByUsername(newTrainer.getUserName());

        assertEquals(newTrainer, resultTrainer.get());
    }

    @Test
    public void getUserByUsernameTest_whenThereIsNoSuchUsername() {
        String wrongUsername = "!wrongUsername!";

        if (trainerDao.ifExistByUsername(wrongUsername)) {
            Trainer newTrainer = trainerDao.getByUsername(wrongUsername).get();
            trainerDao.delete(newTrainer);
        }

        Optional<Trainer> resultTrainer = trainerDao.getByUsername(wrongUsername);

        assertTrue(resultTrainer.isEmpty());
    }

    @Test
    public void getUserByIdTest() {
        Trainer newTrainer = trainerDao.save(trainer);

        Optional<Trainer> resultTrainer = trainerDao.getById(newTrainer.getId());

        assertEquals(resultTrainer.get(), newTrainer);

        trainerDao.delete(newTrainer);

        resultTrainer = trainerDao.getById(newTrainer.getId());

        assertTrue(resultTrainer.isEmpty());
    }

    @Test
    public void ifExistByIdTest() {
        Trainer newTrainer = trainerDao.save(trainer);

        boolean exists = trainerDao.ifExistById(newTrainer.getId());

        assertTrue(exists);

        trainerDao.delete(newTrainer);

        exists = trainerDao.ifExistById(newTrainer.getId());

        assertFalse(exists);
    }

    @Test
    public void ifExistByUsernameTest() {
        Trainer newTrainer = trainerDao.save(trainer);

        String username = newTrainer.getUserName();
        boolean exists = trainerDao.ifExistByUsername(username);

        assertTrue(exists);

        trainerDao.delete(newTrainer);

        exists = trainerDao.ifExistByUsername(username);

        assertFalse(exists);
    }

    @Test
    public void getAllTrainersTest() {
        Trainer trainer2 = Trainer.builder()
                .firstName("Robert")
                .lastName("Martin")
                .userName("Uncle.Bob")
                .password("bob-Pass")
                .active(true)
                .specialization("Java")
                .build();

        trainerDao.save(trainer);
        trainerDao.save(trainer2);

        List<Trainer> trainers = trainerDao.getAll();

        assertThat(trainers).contains(trainer, trainer2);
    }

    @Test
    public void getUnassignedTrainersTest(){
        Trainee traineeBob = Trainee.builder()
                .firstName("Bob")
                .lastName("Santos")
                .userName("Bob.Santos")
                .password("qwerty")
                .active(true)
                .build();

        Trainee traineeAndrew = Trainee.builder()
                .firstName("Andrew")
                .lastName("Sakharov")
                .userName("andrew.sugar")
                .password("afgfd")
                .active(true)
                .build();

        Trainee traineeJohny = Trainee.builder()
                .firstName("John")
                .lastName("Osborn")
                .userName("john.ozzy")
                .password("klafjhgaf")
                .active(true)
                .build();

        Trainer trainerDustin = Trainer.builder()
                .firstName("Dustin")
                .lastName("Poirier")
                .userName("Dustin.Poirier")
                .password("qwerty456")
                .active(true)
                .specialization("MMA")
                .build();

        Trainer trainerKhabib = Trainer.builder()
                .firstName("Khabib")
                .lastName("Nurmagomedov")
                .userName("Khabib.Nurmagomedov")
                .password("qwerty789")
                .active(true)
                .specialization("MMA")
                .build();

        Training training1 = Training.builder()
                .trainingName("Bob's training")
                .trainingDate(LocalDateTime.now())
                .trainingDuration(Duration.ofMinutes(90))
                .trainingType(TrainingType.BACK_TRAINING)
                .trainee(traineeBob)
                .trainer(trainer)
                .build();

        Training training2 = Training.builder()
                .trainingName("MMA training")
                .trainingDate(LocalDateTime.now())
                .trainingDuration(Duration.ofMinutes(60))
                .trainingType(TrainingType.MIXED_MARTIAL_ARTS)
                .trainer(trainerDustin)
                .trainee(traineeAndrew)
                .build();

        Training training3 = Training.builder()
                .trainingName("Cardio")
                .trainingDate(LocalDateTime.now())
                .trainingDuration(Duration.ofMinutes(45))
                .trainingType(TrainingType.CARDIO)
                .trainer(trainerKhabib)
                .trainee(traineeJohny)
                .build();

        traineeDao.save(traineeBob);
        traineeDao.save(traineeAndrew);
        traineeDao.save(traineeJohny);

        trainerDao.save(trainer);
        trainerDao.save(trainerDustin);
        trainerDao.save(trainerKhabib);

        trainingDao.save(training1);
        trainingDao.save(training2);
        trainingDao.save(training3);

        List<Trainer> unassignedTrainers = trainerDao.getUnassignedTrainers(traineeBob.getUserName());

        assertThat(unassignedTrainers).contains(trainerDustin, trainerKhabib);
    }
}
