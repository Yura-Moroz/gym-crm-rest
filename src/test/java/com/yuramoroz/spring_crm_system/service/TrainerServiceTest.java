package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.profileHandlers.PasswordHandler;
import com.yuramoroz.spring_crm_system.repository.TrainerDao;
import com.yuramoroz.spring_crm_system.service.impl.TrainerServiceImpl;
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
        TrainerDto trainerDto = TrainerDto.builder()
                .id(1L)
                .firstName("Jimmy")
                .lastName("Page")
                .userName("Jimmy.Page")
                .password("page1099")
                .active(true)
                .specialization("Lying")
                .build();

        when(trainerDao.ifExistByUsername(anyString())).thenReturn(false);
        when(trainerDao.save(any(Trainer.class))).thenReturn(trainer);

        Trainer savedTrainer = trainerService.save(trainerDto);

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
        TrainerDto trainerUpdatingDto = TrainerDto.builder()
                .id(1L)
                .firstName("Jimmy")
                .lastName("Page")
                .userName("Jimmy.Page")
                .password("page1099")
                .active(true)
                .specialization("Lying")
                .build();

        when(trainerDao.ifExistById(trainer.getId())).thenReturn(true);
        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer updatedTrainer = trainerService.update(trainer, trainerUpdatingDto);

        verify(trainerDao, times(1)).ifExistById(trainer.getId());
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void activateTrainerProfileTest() {
        when(trainerDao.update(any(Trainer.class))).thenReturn(trainer);

        trainer.setActive(false);

        trainerService.activate(trainer);

        assertTrue(trainer.isActive());
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void deactivateTrainerProfileTest() {
        when(trainerDao.update(any(Trainer.class))).thenReturn(trainer);

        trainer.setActive(true);

        trainerService.deactivate(trainer);

        assertFalse(trainer.isActive());
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void changeTrainerProfilePasswordTest() {
        String newPassword = "pass123";
        String oldPassword = trainer.getPassword();

        try (MockedStatic<PasswordHandler> mockedStatic = Mockito.mockStatic(PasswordHandler.class)) {
            mockedStatic.when(() -> PasswordHandler.verify(newPassword)).thenReturn(true);
            mockedStatic.when(() -> PasswordHandler.ifPasswordMatches(oldPassword, trainer.getPassword())).thenReturn(true);
            mockedStatic.when(() -> PasswordHandler.hashPassword(newPassword)).thenReturn(newPassword);
            when(trainerDao.ifExistById(trainer.getId())).thenReturn(true);
            when(trainerDao.update(trainer)).thenReturn(trainer);

            trainerService.changePassword(trainer, oldPassword, newPassword);

            assertEquals(trainer.getPassword(), newPassword);
            verify(trainerDao, times(1)).ifExistById(trainer.getId());
            verify(trainerDao, times(1)).update(trainer);
            mockedStatic.verify(() -> PasswordHandler.hashPassword(newPassword), times(1));
            mockedStatic.verify(() -> PasswordHandler.ifPasswordMatches(oldPassword, oldPassword), times(1));
            mockedStatic.verify(() -> PasswordHandler.verify(newPassword), times(1));
        }
    }

    @Test
    public void getUnassignedTrainersTest(){
        trainerService.getUnassignedTrainersToUserByUsername(trainer.getUserName());

        verify(trainerDao, times(1)).getUnassignedTrainers(trainer.getUserName());
    }

}
