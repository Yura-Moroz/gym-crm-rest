package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.config.TestConfig;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(classes = TestConfig.class)
public class TraineeDaoTest {

    @Autowired
    private TraineeDao traineeDao;

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
        Trainee newTrainee = traineeDao.save(trainee);

        Optional<Trainee> resultTrainee = traineeDao.getById(newTrainee.getId());

        assertEquals(resultTrainee.get(), trainee);

        traineeDao.delete(trainee);
        resultTrainee = traineeDao.getById(trainee.getId());

        assertTrue(resultTrainee.isEmpty());
    }

    @Test
    public void saveTraineeTest() {
        Trainee resultTrainee = traineeDao.save(trainee);

        assertNotNull(resultTrainee.getId());
        assertEquals(resultTrainee.getFirstName(), trainee.getFirstName());
        assertEquals(resultTrainee.getLastName(), trainee.getLastName());
        assertEquals(resultTrainee.getAddress(), trainee.getAddress());
        assertEquals(resultTrainee.getDateOfBirth(), trainee.getDateOfBirth());
        assertEquals(resultTrainee.getUserName(), trainee.getUserName());
        assertEquals(resultTrainee.getPassword(), trainee.getPassword());
        assertEquals(resultTrainee.getTrainings(), trainee.getTrainings());
    }

    @Test
    public void updateTraineeTest() {
        String newPassword = "new_pass";
        String newUsername = "test.user";

        Trainee newTrainee = traineeDao.save(trainee);
        newTrainee.setPassword(newPassword);
        newTrainee.setUserName(newUsername);

        newTrainee = traineeDao.update(trainee);

        assertEquals(newTrainee.getPassword(), newPassword);
        assertEquals(newTrainee.getUserName(), newUsername);
    }

    @Test
    public void deleteUserTest() {
        Trainee newTrainee = traineeDao.save(trainee);

        traineeDao.delete(newTrainee);

        assertFalse(traineeDao.ifExistById(newTrainee.getId()));
    }

    @Test
    public void getUserByUsernameTest_whenUsernameExists() {
        Trainee newTrainee = traineeDao.save(trainee);

        Optional<Trainee> resultTrainee = traineeDao.getByUsername(newTrainee.getUserName());

        assertEquals(trainee, resultTrainee.get());
    }

    @Test
    public void getUserByUsernameTest_whenThereIsNoSuchUsername() {
        String wrongUsername = "!wrongUsername!";

        if (traineeDao.ifExistByUsername(wrongUsername)) {
            Trainee newTrainee = traineeDao.getByUsername(wrongUsername).get();
            traineeDao.delete(newTrainee);
        }

        assertThrows(NoResultException.class, () -> traineeDao.getByUsername("!wrongUser!"));
    }

    @Test
    public void getUserByIdTest() {
        Trainee newTrainee = traineeDao.save(trainee);

        Optional<Trainee> resultTrainee = traineeDao.getById(newTrainee.getId());

        assertEquals(resultTrainee.get(), newTrainee);

        traineeDao.delete(newTrainee);

        resultTrainee = traineeDao.getById(newTrainee.getId());

        assertTrue(resultTrainee.isEmpty());
    }

    @Test
    public void ifExistByIdTest() {
        Trainee newTrainee = traineeDao.save(trainee);

        boolean exists = traineeDao.ifExistById(newTrainee.getId());

        assertTrue(exists);

        traineeDao.delete(newTrainee);

        exists = traineeDao.ifExistById(newTrainee.getId());

        assertFalse(exists);
    }

    @Test
    public void ifExistByUsernameTest() {
        Trainee newTrainee = traineeDao.save(trainee);

        String username = newTrainee.getUserName();
        boolean exists = traineeDao.ifExistByUsername(username);

        assertTrue(exists);

        traineeDao.delete(newTrainee);

        exists = traineeDao.ifExistByUsername(username);

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

}
