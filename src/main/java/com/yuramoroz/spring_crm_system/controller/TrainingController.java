package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingAddingDto;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.service.TrainingService;
import com.yuramoroz.spring_crm_system.views.TrainingViews;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Training Controller", description = "Endpoints for trainings manipulations")
@RequestMapping("/gym-api/trainings")
@RestController
@Validated
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    private final ConversionService conversionService;

    private final MeterRegistry meterRegistry;

    @Operation(summary = "Create a new training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New training created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> addTraining(@RequestBody
                                            @Valid
                                            TrainingAddingDto trainingDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "POST /gym-api/trainings").increment();

        trainingService.save(conversionService.convert(trainingDto, Training.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @Operation(summary = "Get types of all exiting trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all training types from the DB", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/types")
    public ResponseEntity<Map<String, Integer>> getTrainingTypes() {

        meterRegistry.counter("endpoint.calls", "endpoint", "GET /gym-api/trainings/types").increment();

        Map<String, Integer> result = new HashMap<>();
        List<Training> trainings = trainingService.getAll();

        trainings.forEach(training -> result.put(
                training.getTrainingType().toString(), training.getTrainingType().ordinal()));

        return result.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) :
                ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "Get trainee trainings by trainee and trainer usernames and date range",
            parameters = {
                    @Parameter(name = "traineeUsername", description = "Trainee username", required = true),
                    @Parameter(name = "dateFrom", description = "Date from inclusive"),
                    @Parameter(name = "dateTo", description = "Date to inclusive"),
                    @Parameter(name = "trainerUsername", description = "Trainer username", required = true),
                    @Parameter(name = "type", description = "Training type", required = true)
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainings by criteria",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/trainee-trainings-date-range")
    @JsonView(TrainingViews.GetResp.class)
    public ResponseEntity<List<TrainingDto>> getTraineeTrainingsByDateRange(
            @RequestParam(name = "username") String traineeUsername,
            @RequestParam(name = "date-from", required = false) LocalDate dateFrom,
            @RequestParam(name = "date-to", required = false) LocalDate dateTo,
            @RequestParam(name = "trainer-username") String trainerUsername,
            @RequestParam(name = "type") TrainingType type) {

        meterRegistry.counter("endpoint.calls", "endpoint",
                "GET /gym-api/trainings/trainee-trainings-date-range")
                .increment();

        List<Training> traineeTrainingsByCriteria = trainingService.getTrainingsByTraineeUsernameAndDateRange(
                traineeUsername, dateFrom, dateTo, trainerUsername, type);

        List<TrainingDto> result = traineeTrainingsByCriteria.stream()
                .map(training -> conversionService.convert(training, TrainingDto.class))
                .toList();
        return result.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) :
                ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @Operation(summary = "Get trainer trainings by trainer and trainee usernames and date range",
            parameters = {
                    @Parameter(name = "trainerUsername", description = "Trainer username", required = true),
                    @Parameter(name = "dateFrom", description = "Date from inclusive"),
                    @Parameter(name = "dateTo", description = "Date to inclusive"),
                    @Parameter(name = "traineeUsername", description = "Trainee username", required = true),
                    @Parameter(name = "type", description = "Training type", required = true)
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all trainings by criteria",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/trainer-trainings-date-range")
    @JsonView(TrainingViews.GetResp.class)
    public ResponseEntity<List<TrainingDto>> getTrainerTrainingsDateRange(
            @RequestParam(name = "username") String trainerUsername,
            @RequestParam(name = "date-from", required = false) LocalDate dateFrom,
            @RequestParam(name = "date-to", required = false) LocalDate dateTo,
            @RequestParam(name = "trainee-username") String traineeUsername,
            @RequestParam(name = "type") TrainingType type) {

        meterRegistry.counter("endpoint.calls", "endpoint",
                        "GET /gym-api/trainings/trainer-trainings-date-range")
                .increment();

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
