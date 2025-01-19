package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.config.TestConfig;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.impl.TraineeDao;
import com.yuramoroz.spring_crm_system.validation.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeService traineeService;

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
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void saveTraineeWithMultiParam_shouldSaveTraineeSuccessfully() {

        when(traineeDao.ifUserExistByUsername(anyString())).thenReturn(false);
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);

        Trainee savedTrainee = traineeService.saveTrainee(
                "John", "Doe", "password123", "123 Street", LocalDate.of(1990, 1, 1));

        assertNotNull(savedTrainee);
        assertEquals("John", savedTrainee.getFirstName());
        verify(traineeDao, times(1)).ifUserExistByUsername(anyString());
        verify(traineeDao, times(1)).save(any(Trainee.class));
    }

    @Test
    void saveTraineeWithOneParam_shouldSaveTraineeSuccessfully() {
        when(traineeDao.ifUserExistByUsername(anyString())).thenReturn(false);
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);

        Trainee resultTrainee = traineeService.saveUser(trainee);

        assertNotNull(resultTrainee);
        assertEquals("John", resultTrainee.getFirstName());
        verify(traineeDao, times(1)).ifUserExistByUsername(anyString());
        verify(traineeDao, times(1)).save(any(Trainee.class));
    }

    @Test
    void findByIdTest_withExistingTrainee() {
        when(traineeDao.ifExistById(trainee.getId())).thenReturn(true);
        when(traineeDao.getById(trainee.getId())).thenReturn(Optional.of(trainee));

        Trainee resultTrainee = (Trainee) traineeService.selectUserById(trainee.getId());

        assertNotNull(resultTrainee);
        assertEquals(resultTrainee, trainee);
        verify(traineeDao, times(1)).getById(trainee.getId());
        verify(traineeDao, times(1)).ifExistById(trainee.getId());
    }

    @Test
    void findByUsernameTest_ShouldReturnExistingUserByUsername() {
        when(traineeDao.ifUserExistByUsername(trainee.getUserName())).thenReturn(true);
        when(traineeDao.getUserByUsername(trainee.getUserName())).thenReturn(Optional.of(trainee));

        Trainee resultTrainee = (Trainee) traineeService.selectUserByUsername(trainee.getUserName());

        assertNotNull(resultTrainee);
        assertEquals(resultTrainee, trainee);
        verify(traineeDao, times(1)).ifUserExistByUsername(trainee.getUserName());
        verify(traineeDao, times(1)).getUserByUsername(trainee.getUserName());
    }

    @Test
    void getTrainingsByCriteria_shouldReturnTrainings() {
        String username = "johndoe";
        LocalDate dateFrom = LocalDate.of(2023, 1, 1);
        LocalDate dateTo = LocalDate.of(2023, 12, 31);
        String trainerName = "Jane";
        TrainingType trainingType = TrainingType.BOXES;

        Training training = Training.builder()
                .trainingDate(LocalDateTime.of(2023, 6, 15, 16, 15, 0))
                .trainingType(trainingType)
                .build();

        when(traineeDao.ifUserExistByUsername(username)).thenReturn(true);
        when(traineeDao.getTrainingsByCriteria(username, dateFrom, dateTo, trainerName, trainingType))
                .thenReturn(List.of(training));

        List<Training> trainings = traineeService.getTrainingsByCriteria(username, dateFrom, dateTo, trainerName, trainingType);

        assertTrue(trainings.size() > 0);
        assertEquals(trainingType, trainings.get(0).getTrainingType());
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteUserIfExists() {
        String username = "johndoe";
        Trainee trainee = Trainee.builder().userName(username).build();

        when(traineeDao.ifUserExistByUsername(username)).thenReturn(true);
        when(traineeDao.getUserByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deleteUserByUsername(username);

        verify(traineeDao, times(1)).delete(trainee);

    }

    @Test
    void deleteTraineeByUsername_shouldLogWarningIfUserNotFound() {
        String username = "@nonexistent";

        when(traineeDao.ifUserExistByUsername(username)).thenReturn(false);

        traineeService.deleteUserByUsername(username);

        verify(traineeDao, never()).delete(any());
    }

    @Test
    void deleteTraineeByUserArgument() {
        traineeService.deleteUser(trainee);

        verify(traineeDao, times(1)).delete(trainee);
    }

    @Test
    void updateTraineeTest() {
        when(traineeDao.ifExistById(trainee.getId())).thenReturn(true);
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee updatedTrainee = traineeService.updateUser(trainee);

        assertNotNull(updatedTrainee);
        verify(traineeDao, times(1)).ifExistById(trainee.getId());
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void activateTraineeProfileTest() {
        trainee.setActive(false);

        traineeService.activateUser(trainee);

        assertTrue(trainee.isActive());
    }

    @Test
    void deactivateTraineeProfileTest() {
        trainee.setActive(true);

        traineeService.deactivateUser(trainee);

        assertFalse(trainee.isActive());
    }

    @Test
    void changeTraineeProfilePasswordTest() {
        String newPassword = "pass123";
        String oldPassword = trainee.getPassword();

        try (MockedStatic<PasswordValidator> mockedStatic = Mockito.mockStatic(PasswordValidator.class)) {
            mockedStatic.when(() -> PasswordValidator.verify(newPassword)).thenReturn(true);
            mockedStatic.when(() -> PasswordValidator.ifPasswordMatches(oldPassword, trainee.getPassword())).thenReturn(true);
            mockedStatic.when(() -> PasswordValidator.hashPassword(newPassword)).thenReturn(newPassword);
            when(traineeDao.ifExistById(trainee.getId())).thenReturn(true);
            when(traineeDao.update(trainee)).thenReturn(trainee);

            traineeService.changeUserPassword(trainee, oldPassword, newPassword);

            assertEquals(trainee.getPassword(), newPassword);
            verify(traineeDao, times(2)).ifExistById(trainee.getId());
            verify(traineeDao, times(1)).update(trainee);
            mockedStatic.verify(() -> PasswordValidator.hashPassword(newPassword), times(1));
            mockedStatic.verify(() -> PasswordValidator.ifPasswordMatches(oldPassword, oldPassword), times(1));
            mockedStatic.verify(() -> PasswordValidator.verify(newPassword), times(1));
        }
    }
}
