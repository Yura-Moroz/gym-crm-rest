package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuramoroz.spring_crm_system.converters.traineeConverters.TraineeDtoToTraineeEntityConverter;
import com.yuramoroz.spring_crm_system.converters.traineeConverters.TraineeToDtoConverter;
import com.yuramoroz.spring_crm_system.converters.trainingConverters.TrainingEntityToTrainingDtoConverter;
import com.yuramoroz.spring_crm_system.dto.trainees.TraineeDto;
import com.yuramoroz.spring_crm_system.dto.UserLoginDto;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TraineeDtoToTraineeEntityConverter toTraineeEntityConverter;

    @MockitoBean
    private TraineeToDtoConverter toTraineeDtoConverter;

    @MockitoBean
    private TrainingEntityToTrainingDtoConverter toTrainingDtoConverter;

    private Trainee trainee;
    private TraineeDto traineeDto;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("john.doe")
                .password("password123")
                .active(true)
                .address("123 Main St")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        traineeDto = TraineeDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("john.doe")
                .active(true)
                .address("123 Main St")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void shouldCreateTrainee() throws Exception {
        when(toTraineeEntityConverter.convert(any(TraineeDto.class))).thenReturn(trainee);
        when(traineeService.save(any(TraineeDto.class))).thenReturn(trainee);
        when(toTraineeDtoConverter.convert(any(Trainee.class))).thenReturn(traineeDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/gym-api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("john.doe"));
    }

    @Test
    void shouldGetTraineeByUsername() throws Exception {
        when(traineeService.getByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(toTraineeDtoConverter.convert(any(Trainee.class))).thenReturn(traineeDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainees/john.doe")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldUpdateTraineeProfile() throws Exception {
        when(traineeService.getById(1L)).thenReturn(Optional.of(trainee));
        when(traineeService.update(any(Trainee.class), any(TraineeDto.class))).thenReturn(trainee);
        when(toTraineeDtoConverter.convert(any(Trainee.class))).thenReturn(traineeDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void shouldDeleteTraineeProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/gym-api/trainees/john.doe"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldChangePassword() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("john.doe", "oldPass", "newPass");
        PasswordChangingResult result = new PasswordChangingResult(true, "Password changed successfully");

        when(traineeService.getByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeService.changePassword(any(Trainee.class), any(), any())).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainees/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    void shouldChangeTraineeStatus() throws Exception {
        when(traineeService.getByUsername("john.doe")).thenReturn(Optional.of(trainee));

        mockMvc.perform(MockMvcRequestBuilders.patch("/gym-api/trainees/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getProfileByUsername_NotFound() throws Exception {
        when(traineeService.getByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainees/nonexistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("The user was not found"));
    }

    @Test
    void changePassword_InvalidOldPassword() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("user", "wrongOldPass", "newPass");
        Trainee trainee = new Trainee();
        when(traineeService.getByUsername("user")).thenReturn(Optional.of(trainee));
        when(traineeService.changePassword(any(), any(), any())).thenReturn(
                new PasswordChangingResult(false, "Sorry, It seems that you've provided wrong old password"));

        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainees/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Sorry, It seems that you've provided wrong old password"));
    }

    @Test
    void changePassword_InvalidNewPassword() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("user", "oldPassword", "InvalidNewPass");
        Trainee trainee = new Trainee();
        when(traineeService.getByUsername("user")).thenReturn(Optional.of(trainee));
        when(traineeService.changePassword(any(), any(), any())).thenReturn(
                new PasswordChangingResult(false, "Please check that your new password meets all requirements (length should be 4-10 chars)"));

        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainees/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please check that your new password meets all requirements (length should be 4-10 chars)"));
    }

    @Test
    void updateTrainingsList_Success() throws Exception {
        Trainee trainee = new Trainee();
        Training training = Training.builder()
                .trainingName("Yoga")
                .trainingType(TrainingType.STRETCHING)
                .trainingDate(LocalDateTime.of(2025, 1, 1, 10, 0))
                .trainingDuration(Duration.ofMinutes(60))
                .build();
        trainee.setTrainings(List.of(training));

        List<TrainingDto> trainingDtos = List.of(
                TrainingDto.builder()
                        .trainingName("Legs")
                        .type(TrainingType.LEGS_DAY)
                        .date(LocalDateTime.of(2025, 3, 17, 16, 0, 0))
                        .duration(Duration.ofMinutes(90))
                        .build()
        );

        when(traineeService.getByUsername(any())).thenReturn(Optional.of(trainee));
        when(traineeService.update(any(Trainee.class), any(TraineeDto.class))).thenReturn(trainee);
        when(toTrainingDtoConverter.convert(any(Training.class))).thenReturn(trainingDtos.get(0));

        mockMvc.perform(MockMvcRequestBuilders.put("/gym-api/trainees/username/update-trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingDtos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // Ensure "trainings" is a list
                .andExpect(jsonPath("$[0].trainingName").value("Legs"))
                .andExpect(jsonPath("$[0].type").value("LEGS_DAY"))
                .andExpect(jsonPath("$[0].date").value("2025-03-17T16:00:00"))
                .andExpect(jsonPath("$[0].duration").value("PT1H30M"));
    }

}
