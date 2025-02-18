package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.dto.trainees.*;
import com.yuramoroz.spring_crm_system.dto.UserLoginDto;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingAddingDto;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import com.yuramoroz.spring_crm_system.views.TraineeViews;
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

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Trainee Controller", description = "Endpoints for managing trainee profiles")
@RequestMapping("/gym-api/trainees")
@RestController
@Validated
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    private final ConversionService conversionService;

    private final MeterRegistry meterRegistry;


    @Operation(summary = "Create a new trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraineeDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping
    @JsonView(TraineeViews.Login.class)
    public ResponseEntity<TraineeDto> createProfile(
            @Parameter(description = "Trainee details for creation", required = true)
            @RequestBody
            @Valid
            @JsonView(TraineeViews.Input.class) TraineeDto traineeDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "POST /gym-api/trainees").increment();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversionService.convert(traineeService.save(traineeDto), TraineeDto.class));
    }

    @Operation(summary = "Change password for a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @Parameter(description = "User login details containing old and new passwords", required = true)
            @RequestBody @Valid UserLoginDto userLoginDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "PUT /gym-api/trainees/password").increment();

        Trainee trainee = traineeService.getByUsername(userLoginDto.getUserName()).get();

        PasswordChangingResult result = traineeService.changePassword(trainee, userLoginDto.getOldPassword(), userLoginDto.getNewPassword());
        return result.isSucceed() ?
                new ResponseEntity<>(result.getMessage(), HttpStatus.OK) :
                new ResponseEntity<>(result.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Retrieve a trainee profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraineeDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/{username}")
    @JsonView(TraineeViews.GetResp.class)
    public ResponseEntity<TraineeDto> getProfileByUsername(
            @Parameter(description = "Username of the trainee", required = true)
            @PathVariable String username) {

        meterRegistry.counter("endpoint.calls", "endpoint", "GET /gym-api/trainees/{username}")
                .increment();

        TraineeDto profileDto = conversionService.convert(traineeService.getByUsername(username).get(), TraineeDto.class);
        return ResponseEntity.ok(profileDto);
    }

    @Operation(summary = "Update an existing trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraineeDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PutMapping("/{id}")
    @JsonView(TraineeViews.UpdateResp.class)
    public ResponseEntity<TraineeDto> updateProfile(
            @Parameter(description = "ID of the trainee to update", required = true)
            @PathVariable long id,
            @Parameter(description = "Updated trainee details", required = true)
            @RequestBody @Valid
            @JsonView(TraineeViews.UpdateReq.class) TraineeDto traineeUpdatingDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "PUT /gym-api/trainees/{id}").increment();

        return ResponseEntity.status(HttpStatus.OK)
                .body(conversionService.convert(traineeService.update(id, traineeUpdatingDto), TraineeDto.class));
    }


    @Operation(summary = "Delete a trainee profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)

    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteProfile(
            @Parameter(description = "Username of the trainee to delete", required = true)
            @PathVariable String username) {

        meterRegistry.counter("endpoint.calls", "endpoint", "DELETE /gym-api/trainees/{username}")
                .increment();

        traineeService.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Update trainings list for a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PutMapping("/{username}/update-trainings")
    @JsonView(TrainingViews.UpdateTraineeTrainings.class)
    public ResponseEntity<List<TrainingDto>> updateTrainingsList(
            @Parameter(description = "Username of the trainee", required = true)
            @PathVariable String username,
            @Parameter(description = "List of trainings to update", required = true)
            @RequestBody @Valid
            List<TrainingAddingDto> trainingsDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "PUT /gym-api/trainees/{username}/update-trainings")
                .increment();

        Trainee trainee = traineeService.getByUsername(username).get();
        List<Training> newTrainings = trainingsDto.stream()
                .map(trainingDto -> conversionService.convert(trainingDto, Training.class)).collect(Collectors.toList());

        Trainee updatedTrainee = traineeService.updateTrainings(trainee, newTrainings);

        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedTrainee.getTrainings()
                        .stream()
                        .map(training -> conversionService.convert(training, TrainingDto.class))
                        .collect(Collectors.toList()));
    }

    @Operation(summary = "Toggle the active status of a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status toggled successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> changeStatus(
            @Parameter(description = "Trainee details with username for status update", required = true)
            @RequestBody @Valid
            @JsonView(TraineeViews.Status.class) TraineeDto traineeDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "PATCH /gym-api/trainees/status").increment();

        Trainee trainee = traineeService.getByUsername(traineeDto.getUserName()).get();
        if (trainee.isActive()) traineeService.deactivate(trainee);
        else traineeService.activate(trainee);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
