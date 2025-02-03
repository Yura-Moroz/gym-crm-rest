package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.profileHandlers.PasswordHandler;
import com.yuramoroz.spring_crm_system.repository.TraineeDao;
import com.yuramoroz.spring_crm_system.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

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
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void saveTraineeWithMultiParam_shouldSaveTraineeSuccessfully() {

        when(traineeDao.ifExistByUsername(anyString())).thenReturn(false);
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);

        Trainee savedTrainee = traineeService.save(
                "Matt", "Watson", "qwerty123", "456 Street", LocalDate.of(1980, 6, 19));

        verify(traineeDao, times(1)).ifExistByUsername(anyString());
        verify(traineeDao, times(1)).save(any(Trainee.class));
    }

    @Test
    void saveTraineeWithOneParam_shouldSaveTraineeSuccessfully() {
        when(traineeDao.ifExistByUsername(anyString())).thenReturn(false);
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);

        Trainee savedTrainee = traineeService.save(trainee);

        verify(traineeDao, times(1)).ifExistByUsername(anyString());
        verify(traineeDao, times(1)).save(any(Trainee.class));
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
    void updateTraineeTest() {
        when(traineeDao.ifExistById(trainee.getId())).thenReturn(true);
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee updatedTrainee = traineeService.update(trainee);

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
            verify(traineeDao, times(2)).ifExistById(trainee.getId());
            verify(traineeDao, times(1)).update(trainee);
            mockedStatic.verify(() -> PasswordHandler.hashPassword(newPassword), times(1));
            mockedStatic.verify(() -> PasswordHandler.ifPasswordMatches(oldPassword, oldPassword), times(1));
            mockedStatic.verify(() -> PasswordHandler.verify(newPassword), times(1));
        }
    }
}
