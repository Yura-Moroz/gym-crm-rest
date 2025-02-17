package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.dto.UserLoginDto;
import com.yuramoroz.spring_crm_system.dto.trainees.TraineeDto;
import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import com.yuramoroz.spring_crm_system.views.TrainerViews;
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

@Tag(name = "Trainer Controller", description = "Endpoints for managing trainer profiles")
@RequestMapping("/gym-api/trainers")
@RestController
@Validated
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    private final ConversionService conversionService;

    private final MeterRegistry meterRegistry;

    @Operation(summary = "Create a new trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping
    @JsonView(TrainerViews.Login.class)
    public ResponseEntity<TrainerDto> createProfile(
            @Parameter(description = "Trainer details for creation", required = true)
            @RequestBody @Valid
            @JsonView(TrainerViews.Input.class) TrainerDto trainerDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "POST /gym-api/trainers").increment();

        return ResponseEntity.status(HttpStatus.CREATED).
                body(conversionService.convert(trainerService.save(trainerDto), TrainerDto.class));
    }


    @Operation(summary = "Change password for a trainer")
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
            @Parameter(description = "User login details containing old and new passwords ", required = true)
            @RequestBody @Valid UserLoginDto userLoginDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "PUT /gym-api/trainers/password").increment();

        Trainer trainer = trainerService.getByUsername(userLoginDto.getUserName()).get();
        PasswordChangingResult result = trainerService.changePassword(trainer, userLoginDto.getOldPassword(), userLoginDto.getNewPassword());
        return result.isSucceed() ?
                new ResponseEntity<>(result.getMessage(), HttpStatus.OK) :
                new ResponseEntity<>(result.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "Retrieve a trainer profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/{username}")
    @JsonView(TrainerViews.GetResp.class)
    public ResponseEntity<TrainerDto> getProfileByUsername(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable String username) {

        meterRegistry.counter("endpoint.calls", "endpoint", "GET /gym-api/trainers/{username}").increment();

        TrainerDto profileDto = conversionService.convert(trainerService.getByUsername(username).get(), TrainerDto.class);
        return ResponseEntity.ok(profileDto);
    }


    @Operation(summary = "Update an existing trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PutMapping("/{id}")
    @JsonView(TrainerViews.UpdatingResp.class)
    public ResponseEntity<TrainerDto> updateProfile(
            @Parameter(description = "ID of the trainer to update", required = true)
            @PathVariable long id,
            @Parameter(description = "Updated trainee details", required = true)
            @RequestBody
            @JsonView(TrainerViews.UpdatingReq.class)
            @Valid TrainerDto trainerUpdatingDto) {

        meterRegistry.counter("endpoint.calls", "endpoint", "PUT /gym-api/trainers/{id}").increment();

        return ResponseEntity.status(HttpStatus.OK)
                .body(conversionService.convert(trainerService.update(id, trainerUpdatingDto), TrainerDto.class));
    }


    @Operation(summary = "Get unassigned trainers to user by trainee username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)

    })
    @GetMapping("/{username}/unassigned")
    @JsonView(TrainerViews.Unassigned.class)
    public ResponseEntity<List<TrainerDto>> getUnassignedTrainersByTraineeUsername(
            @Parameter(description = "Username of the trainee to get unassigned trainers", required = true)
            @PathVariable String username) {

        meterRegistry.counter("endpoint.calls", "endpoint", "GET /gym-api/trainers/{username}/unassigned")
                .increment();

        List<Trainer> unassignedTrainers = trainerService.getUnassignedTrainersToUserByUsername(username);
        List<TrainerDto> resultList = unassignedTrainers
                .stream()
                .map(trainer -> conversionService.convert(trainer, TrainerDto.class))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(resultList);
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
            @Parameter(description = "Trainer details with username for status update", required = true)
            @RequestBody @Valid
            @JsonView(TrainerViews.Status.class)
            TrainerDto trainerDto) {

        meterRegistry.counter("endpoint.trainer.calls", "endpoint", "PATCH /gym-api/trainers/status").increment();

        Trainer trainer = trainerService.getByUsername(trainerDto.getUserName()).get();
        if (trainer.isActive()) trainerService.deactivate(trainer);
        else trainerService.activate(trainer);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
