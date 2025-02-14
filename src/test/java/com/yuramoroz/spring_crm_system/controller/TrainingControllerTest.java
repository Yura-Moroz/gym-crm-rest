package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuramoroz.spring_crm_system.converters.trainingConverters.TrainingAddingDtoToTrainingConverter;
import com.yuramoroz.spring_crm_system.converters.trainingConverters.TrainingEntityToTrainingDtoConverter;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingAddingDto;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithUserDetails
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingService trainingService;

    @MockitoBean
    private TrainingAddingDtoToTrainingConverter addingDtoToTrainingConverter;

    @MockitoBean
    private TrainingEntityToTrainingDtoConverter trainingDtoConverter;

    private TrainingAddingDto trainingAddingDto;
    private Training training;
    private TrainingDto trainingDto;

    @BeforeEach
    void setUp() {
        trainingAddingDto = TrainingAddingDto.builder()
                .traineeUsername("john.doe")
                .trainerUsername("trainer.one")
                .trainingName("Fitness")
                .date(LocalDateTime.of(2025, 8, 12, 17, 15))
                .duration(Duration.ofMinutes(45))
                .type(TrainingType.FITNESS)
                .build();

        training = Training.builder()
                .trainingName("Fitness")
                .trainingType(TrainingType.FITNESS)
                .trainingDate(LocalDateTime.of(2025, 8, 12, 17, 15))
                .trainingDuration(Duration.ofMinutes(45))
                .build();

        trainingDto = TrainingDto.builder()
                .id(1L)
                .trainingName("Fitness")
                .type(TrainingType.FITNESS)
                .date(LocalDateTime.of(2025, 8, 12, 17, 15))
                .duration(Duration.ofMinutes(45))
                .build();
    }

    @Test
    void addTraining_Success() throws Exception {
        when(addingDtoToTrainingConverter.convert(any(TrainingAddingDto.class)))
                .thenReturn(training);
        when(trainingService.save(any(Training.class))).thenReturn(training);

        String jsonInput = objectMapper.writeValueAsString(trainingAddingDto);

        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/gym-api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        //Then
        verify(addingDtoToTrainingConverter, times(1)).convert(any(TrainingAddingDto.class));
        verify(trainingService, times(1)).save(any(Training.class));
    }

    @Test
    void getTrainingTypes_Success() throws Exception {
        // Given:
        Training training2 = Training.builder()
                .trainingName("Yoga")
                .trainingType(TrainingType.STRETCHING)
                .trainingDate(LocalDateTime.of(2025, 9, 1, 10, 0))
                .trainingDuration(Duration.ofMinutes(60))
                .build();

        List<Training> trainingList = List.of(training, training2);
        when(trainingService.getAll()).thenReturn(trainingList);

        //When:
        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainings/types")
                        .accept(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.FITNESS").value(TrainingType.FITNESS.ordinal()))
                .andExpect(jsonPath("$.STRETCHING").value(TrainingType.STRETCHING.ordinal()));
    }

    @Test
    void getTraineeTrainingsByDateRange_Success() throws Exception {
        // Given:
        List<Training> trainingList = List.of(training);
        when(trainingService.getTrainingsByTraineeUsernameAndDateRange(
                eq("john.doe"),
                any(), any(), eq("trainer.one"), eq(TrainingType.FITNESS)))
                .thenReturn(trainingList);

        when(trainingDtoConverter.convert(any(Training.class))).thenReturn(trainingDto);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainings/trainee-trainings-date-range")
                        .param("username", "john.doe")
                        .param("trainer-username", "trainer.one")
                        .param("type", "FITNESS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].trainingName").value("Fitness"))
                .andExpect(jsonPath("$[0].type").value("FITNESS"));

        //Then
        verify(trainingService, times(1))
                .getTrainingsByTraineeUsernameAndDateRange(eq("john.doe"), any(), any(), eq("trainer.one"), eq(TrainingType.FITNESS));
        verify(trainingDtoConverter, times(1)).convert(any(Training.class));
    }

    @Test
    void getTraineeTrainingsByDateRange_NotFound() throws Exception {
        // Given:
        when(trainingService.getTrainingsByTraineeUsernameAndDateRange(
                eq("john.doe"),
                any(), any(), eq("trainer.one"), eq(TrainingType.FITNESS)))
                .thenReturn(List.of());

        // When:
        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainings/trainee-trainings-date-range")
                        .param("username", "john.doe")
                        .param("trainer-username", "trainer.one")
                        .param("type", "FITNESS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        //Then
        verify(trainingService, times(1)).getTrainingsByTraineeUsernameAndDateRange(
                eq("john.doe"), any(), any(), eq("trainer.one"), eq(TrainingType.FITNESS)
        );
    }

    @Test
    void getTrainerTrainingsDateRange_Success() throws Exception {
        // Given:
        List<Training> trainingList = List.of(training);
        when(trainingService.getTrainingsByTrainerUsernameAndDateRange(
                eq("trainer.one"),
                any(), any(), eq("john.doe"), eq(TrainingType.FITNESS)))
                .thenReturn(trainingList);

        when(trainingDtoConverter.convert(any(Training.class))).thenReturn(trainingDto);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainings/trainer-trainings-date-range")
                        .param("username", "trainer.one")
                        .param("trainee-username", "john.doe")
                        .param("type", "FITNESS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].trainingName").value("Fitness"))
                .andExpect(jsonPath("$[0].type").value("FITNESS"));

        //Then
        verify(trainingService, times(1))
                .getTrainingsByTrainerUsernameAndDateRange(eq("trainer.one"), any(), any(), eq("john.doe"), eq(TrainingType.FITNESS));
        verify(trainingDtoConverter, times(1)).convert(any(Training.class));
    }

    @Test
    void getTrainerTrainingsDateRange_NotFound() throws Exception {
        // Given:
        when(trainingService.getTrainingsByTrainerUsernameAndDateRange(
                eq("trainer.one"),
                any(), any(), eq("john.doe"), eq(TrainingType.FITNESS)))
                .thenReturn(List.of());

        // When:
        mockMvc.perform(MockMvcRequestBuilders.get("/gym-api/trainings/trainer-trainings-date-range")
                        .param("username", "trainer.one")
                        .param("trainee-username", "john.doe")
                        .param("type", "FITNESS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        //Then
        verify(trainingService, times(1)).getTrainingsByTrainerUsernameAndDateRange(
                eq("trainer.one"), any(), any(), eq("john.doe"), eq(TrainingType.FITNESS)
        );
    }
}
