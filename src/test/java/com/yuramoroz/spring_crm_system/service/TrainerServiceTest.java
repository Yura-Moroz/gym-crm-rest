package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.repository.TrainerDao;
import com.yuramoroz.spring_crm_system.service.impl.TrainerServiceImpl;
import com.yuramoroz.spring_crm_system.validation.PasswordManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        trainer = Trainer.builder()
                .id(1L)
                .firstName("Conor")
                .lastName("Mcgregor")
                .userName("Conor.Mcgregor")
                .password("qwerty1099")
                .active(true)
                .specialization("MMA")
                .build();
    }

    @Test
    void saveTrainerWithMultiParam_shouldSaveTrainerSuccessfully() {

        when(trainerDao.ifExistByUsername(anyString())).thenReturn(false);
        when(trainerDao.save(any(Trainer.class))).thenReturn(trainer);

        Trainer savedTrainer = trainerService.save(
                "Matt", "Watson", "qwerty123", "Powerlifting");

        verify(trainerDao, times(1)).ifExistByUsername(anyString());
        verify(trainerDao, times(1)).save(any(Trainer.class));
    }

    @Test
    void saveTrainerWithOneParam_shouldSaveTrainerSuccessfully() {
        when(trainerDao.ifExistByUsername(anyString())).thenReturn(false);
        when(trainerDao.save(any(Trainer.class))).thenReturn(trainer);

        Trainer savedTrainer = trainerService.save(trainer);

        verify(trainerDao, times(1)).ifExistByUsername(anyString());
        verify(trainerDao, times(1)).save(any(Trainer.class));
    }

    @Test
    void findByIdTest_withExistingTrainer() {
        when(trainerDao.getById(trainer.getId())).thenReturn(Optional.of(trainer));

        Trainer resultTrainer = trainerService.getById(trainer.getId()).get();

        verify(trainerDao, times(1)).getById(trainer.getId());
    }

    @Test
    void findByUsernameTest_ShouldReturnExistingUserByUsername() {
        when(trainerDao.getByUsername(trainer.getUserName())).thenReturn(Optional.of(trainer));

        Trainer resultTrainer = trainerService.getByUsername(trainer.getUserName()).get();

        verify(trainerDao, times(1)).getByUsername(trainer.getUserName());
    }

    @Test
    void deleteTrainerByUserArgument() {
        trainerService.delete(trainer);

        verify(trainerDao, times(1)).delete(trainer);
    }

    @Test
    void updateTrainerTest() {
        when(trainerDao.ifExistById(trainer.getId())).thenReturn(true);
        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer updatedTrainer = trainerService.update(trainer);

        assertNotNull(updatedTrainer.getId());
        assertEquals(updatedTrainer.getFirstName(), trainer.getFirstName());
        assertEquals(updatedTrainer.getLastName(), trainer.getLastName());
        assertEquals(updatedTrainer.getSpecialization(), trainer.getSpecialization());
        assertEquals(updatedTrainer.getUserName(), trainer.getUserName());
        assertEquals(updatedTrainer.getPassword(), trainer.getPassword());
        assertEquals(updatedTrainer.isActive(), trainer.isActive());
        verify(trainerDao, times(1)).ifExistById(trainer.getId());
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void activateTrainerProfileTest() {
        trainer.setActive(false);

        trainerService.activate(trainer);

        assertTrue(trainer.isActive());
    }

    @Test
    void deactivateTrainerProfileTest() {
        trainer.setActive(true);

        trainerService.deactivate(trainer);

        assertFalse(trainer.isActive());
    }

    @Test
    void changeTrainerProfilePasswordTest() {
        String newPassword = "pass123";
        String oldPassword = trainer.getPassword();

        try (MockedStatic<PasswordManager> mockedStatic = Mockito.mockStatic(PasswordManager.class)) {
            mockedStatic.when(() -> PasswordManager.verify(newPassword)).thenReturn(true);
            mockedStatic.when(() -> PasswordManager.ifPasswordMatches(oldPassword, trainer.getPassword())).thenReturn(true);
            mockedStatic.when(() -> PasswordManager.hashPassword(newPassword)).thenReturn(newPassword);
            when(trainerDao.ifExistById(trainer.getId())).thenReturn(true);
            when(trainerDao.update(trainer)).thenReturn(trainer);

            trainerService.changePassword(trainer, oldPassword, newPassword);

            assertEquals(trainer.getPassword(), newPassword);
            verify(trainerDao, times(2)).ifExistById(trainer.getId());
            verify(trainerDao, times(1)).update(trainer);
            mockedStatic.verify(() -> PasswordManager.hashPassword(newPassword), times(1));
            mockedStatic.verify(() -> PasswordManager.ifPasswordMatches(oldPassword, oldPassword), times(1));
            mockedStatic.verify(() -> PasswordManager.verify(newPassword), times(1));
        }
    }

    @Test
    public void getUnassignedTrainersTest(){
        trainerService.getUnassignedTrainersToUserByUsername(trainer.getUserName());

        verify(trainerDao, times(1)).getUnassignedTrainers(trainer.getUserName());
    }

}
