package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuramoroz.spring_crm_system.converters.trainerConverters.TrainerEntityToTrainerDtoConverter;
import com.yuramoroz.spring_crm_system.dto.UserLoginDto;
import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import com.yuramoroz.spring_crm_system.views.TrainerViews;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private TrainerEntityToTrainerDtoConverter toTrainerDtoConverter;

    private Trainer trainer;
    private TrainerDto trainerDto;

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .id(1L)
                .firstName("Peter")
                .lastName("Pranker")
                .userName("peter.pranker")
                .password("peterPass")
                .active(true)
                .specialization("Powerlifting")
                .build();

        trainerDto = TrainerDto.builder()
                .id(1L)
                .firstName("Peter")
                .lastName("Pranker")
                .userName("peter.pranker")
                .password("peterPass")
                .active(true)
                .specialization("Powerlifting")
                .build();
    }

    @Test
    void createProfile_Success() throws Exception {
        when(trainerService.save(any(TrainerDto.class))).thenReturn(trainer);
        when(toTrainerDtoConverter.convert(any(Trainer.class))).thenReturn(trainerDto);

        String jsonInput = objectMapper.writeValueAsString(trainerDto);

        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/gym-api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("peter.pranker"));

        //Then
        verify(trainerService, times(1)).save(any(TrainerDto.class));
        verify(toTrainerDtoConverter, times(1)).convert(any(Trainer.class));
    }

    @Test
    void changePassword_Success() throws Exception {
        //Given
        UserLoginDto loginDto = UserLoginDto.builder()
                .userName("peter.pranker")
                .oldPassword("peterPass")
                .newPassword("newPeterPass")
                .build();

        PasswordChangingResult result = new PasswordChangingResult(true, "Password changed successfully");

        when(trainerService.getByUsername("peter.pranker")).thenReturn(Optional.of(trainer));
        when(trainerService.changePassword(any(Trainer.class), any(), any())).thenReturn(result);

        //When
        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainers/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));

        //Then
        verify(trainerService, times(1)).getByUsername("peter.pranker");
        verify(trainerService, times(1)).changePassword(any(Trainer.class), anyString(), anyString());
    }

    @Test
    void changePassword_Failure() throws Exception {
        //Given
        UserLoginDto loginDto = UserLoginDto.builder()
                .userName("peter.pranker")
                .oldPassword("wrongPass")
                .newPassword("newPeterPass")
                .build();

        PasswordChangingResult result = new PasswordChangingResult(false, "Wrong old password");

        when(trainerService.getByUsername("peter.pranker")).thenReturn(Optional.of(trainer));
        when(trainerService.changePassword(any(Trainer.class), anyString(), anyString())).thenReturn(result);

        //When
        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainers/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wrong old password"));

        //Then
        verify(trainerService, times(1)).getByUsername("peter.pranker");
        verify(trainerService, times(1)).changePassword(any(Trainer.class), anyString(), anyString());
    }

    @Test
    void getProfileByUsername_Success() throws Exception {
        when(trainerService.getByUsername("peter.pranker")).thenReturn(Optional.of(trainer));
        when(toTrainerDtoConverter.convert(any(Trainer.class))).thenReturn(trainerDto);

        //When
        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainers/peter.pranker")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Peter"))
                .andExpect(jsonPath("$.lastName").value("Pranker"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.specialization").value("Powerlifting"));

        //Then
        verify(trainerService, times(1)).getByUsername("peter.pranker");
        verify(toTrainerDtoConverter, times(1)).convert(any(Trainer.class));
    }

    @Test
    void updateProfile_Success() throws Exception {
        //Given
        Trainer updatedTrainer = Trainer.builder()
                .id(1L)
                .firstName("Peter")
                .lastName("Pranker")
                .userName("peter.updated")
                .password("peterPass")
                .active(false)
                .specialization("Powerlifting")
                .build();
        TrainerDto updatedTrainerDto = TrainerDto.builder()
                .id(1L)
                .firstName("Peter")
                .lastName("Pranker")
                .userName("peter.updated")
                .password("peterPass")
                .active(false)
                .specialization("Powerlifting")
                .build();

        when(trainerService.getById(1L)).thenReturn(Optional.of(trainer));
        when(trainerService.update(anyLong(), any(TrainerDto.class))).thenReturn(updatedTrainer);
        when(toTrainerDtoConverter.convert(any(Trainer.class))).thenReturn(updatedTrainerDto);

        String jsonInput = objectMapper
                .writerWithView(TrainerViews.UpdatingReq.class)
                .writeValueAsString(trainerDto);

        //When
        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("peter.updated"))
                .andExpect(jsonPath("$.active").value(false));

        //Then
        verify(trainerService, times(1)).update(anyLong(), any(TrainerDto.class));
        verify(toTrainerDtoConverter, times(1)).convert(any(Trainer.class));
    }

    @Test
    void getUnassignedTrainersByTraineeUsername_Success() throws Exception {
        //Given
        Trainer unassignedTrainer = Trainer.builder()
                .id(2L)
                .firstName("Alex")
                .lastName("Smith")
                .userName("alex.smith")
                .active(true)
                .specialization("Bodybuilding")
                .build();

        TrainerDto unassignedTrainerDto = TrainerDto.builder()
                .id(2L)
                .firstName("Alex")
                .lastName("Smith")
                .userName("alex.smith")
                .active(true)
                .specialization("Bodybuilding")
                .build();

        when(trainerService.getUnassignedTrainersToUserByUsername("peter.pranker"))
                .thenReturn(List.of(unassignedTrainer));
        when(toTrainerDtoConverter.convert(any(Trainer.class))).thenReturn(unassignedTrainerDto);

        //When
        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainers/peter.pranker/unassigned")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userName").value("alex.smith"));

        //Then
        verify(toTrainerDtoConverter, times(1)).convert(any(Trainer.class));
        verify(trainerService, times(1)).getUnassignedTrainersToUserByUsername("peter.pranker");
    }

    @Test
    void changeStatus_Success() throws Exception {
        when(trainerService.getByUsername("peter.pranker")).thenReturn(Optional.of(trainer));
        String jsonInput = objectMapper
                .writerWithView(TrainerViews.Status.class)
                .writeValueAsString(trainerDto);

        //When
        mockMvc.perform(MockMvcRequestBuilders.patch("/gym-api/trainers/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isOk());

        //Then
        verify(trainerService, times(1)).getByUsername("peter.pranker");
    }
}

