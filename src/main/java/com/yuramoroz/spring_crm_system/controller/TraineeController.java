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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RequestMapping("/gym-api/trainees")
@RestController
@Validated
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    private final ConversionService conversionService;


    @PostMapping
    @JsonView(TraineeViews.Login.class)
    public ResponseEntity<TraineeDto> createProfile(@RequestBody @Valid
                                                    @JsonView(TraineeViews.Input.class) TraineeDto traineeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversionService.convert(traineeService.save(traineeDto), TraineeDto.class));
    }


    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid UserLoginDto userLoginDto) {
        Trainee trainee = traineeService.getByUsername(userLoginDto.getUserName()).get();

        PasswordChangingResult result = traineeService.changePassword(trainee, userLoginDto.getOldPassword(), userLoginDto.getNewPassword());
        return result.isSucceed() ?
                new ResponseEntity<>(result.getMessage(), HttpStatus.OK) :
                new ResponseEntity<>(result.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/{username}")
    @JsonView(TraineeViews.GetResp.class)
    public ResponseEntity<TraineeDto> getProfileByUsername(@PathVariable String username) {
        TraineeDto profileDto = conversionService.convert(traineeService.getByUsername(username).get(), TraineeDto.class);
        return ResponseEntity.ok(profileDto);
    }


    @PutMapping("/{id}")
    @JsonView(TraineeViews.UpdateResp.class)
    public ResponseEntity<TraineeDto> updateProfile(@PathVariable long id,
                                                    @RequestBody @Valid
                                                    @JsonView(TraineeViews.UpdateReq.class) TraineeDto traineeUpdatingDto) {
        Trainee trainee = traineeService.getById(id).get();
        return ResponseEntity.status(HttpStatus.OK)
                .body(conversionService.convert(traineeService.update(trainee, traineeUpdatingDto), TraineeDto.class));
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PutMapping("/{username}/update-trainings")
    @JsonView(TrainingViews.UpdateTraineeTrainings.class)
    public ResponseEntity<List<TrainingDto>> updateTrainingsList(@PathVariable String username,
                                                                 @RequestBody @Valid
                                                                 List<TrainingAddingDto> trainingsDto) {
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


    @PatchMapping("/status")
    public ResponseEntity<Void> changeStatus(@RequestBody
                                             @Valid
                                             @JsonView(TraineeViews.Status.class) TraineeDto traineeDto) {
        Trainee trainee = traineeService.getByUsername(traineeDto.getUserName()).get();
        if (trainee.isActive()) traineeService.deactivate(trainee);
        else traineeService.activate(trainee);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
