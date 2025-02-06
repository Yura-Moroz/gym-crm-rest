package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingAddingDto;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.service.TrainingService;
import com.yuramoroz.spring_crm_system.views.TrainingViews;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/gym-api/trainings")
@RestController
@Validated
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    private final ConversionService conversionService;


    @PostMapping
    public ResponseEntity<Void> addTraining(@RequestBody
                                            @Valid
                                            TrainingAddingDto trainingDto) {
        trainingService.save(conversionService.convert(trainingDto, Training.class));
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @GetMapping("/types")
    public ResponseEntity<Map<String, Integer>> getTrainingTypes() {

        Map<String, Integer> result = new HashMap<>();
        List<Training> trainings = trainingService.getAll();

        for (Training training : trainings) {
            result.put(training.getTrainingType().toString(), training.getTrainingType().ordinal());
        }

        return result.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) :
                ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/trainee-trainings-date-range")
    @JsonView(TrainingViews.GetTraineeResp.class)
    public ResponseEntity<List<TrainingDto>> getTraineeTrainingsByDateRange(
            @RequestParam(name = "username", required = true) String traineeUsername,
            @RequestParam(name = "date-from", required = false) LocalDate dateFrom,
            @RequestParam(name = "date-to", required = false) LocalDate dateTo,
            @RequestParam(name = "trainer-username", required = true) String trainerUsername,
            @RequestParam(name = "type", required = false) TrainingType type) {

        List<Training> traineeTrainingsByCriteria = trainingService.getTrainingsByTraineeUsernameAndDateRange(
                traineeUsername, dateFrom, dateTo, trainerUsername, type);

        List<TrainingDto> result = traineeTrainingsByCriteria.stream()
                .map(training -> conversionService.convert(training, TrainingDto.class))
                .toList();
        return result.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) :
                ResponseEntity.status(HttpStatus.OK).body(result);

    }


    @GetMapping("/trainer-trainings-date-range")
    @JsonView(TrainingViews.GetTrainerResp.class)
    public ResponseEntity<List<TrainingDto>> getTrainerTrainingsDateRange(
            @RequestParam(name = "username", required = true) String trainerUsername,
            @RequestParam(name = "date-from", required = false) LocalDate dateFrom,
            @RequestParam(name = "date-to", required = false) LocalDate dateTo,
            @RequestParam(name = "trainee-username", required = true) String traineeUsername,
            @RequestParam(name = "type", required = false) TrainingType type) {

        List<Training> trainerTrainingsByCriteria = trainingService.getTrainingsByTrainerUsernameAndDateRange(
                trainerUsername, dateFrom, dateTo, traineeUsername, type);

        List<TrainingDto> result = trainerTrainingsByCriteria.stream()
                .map(training -> conversionService.convert(training, TrainingDto.class))
                .toList();
        return result.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) :
                ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
