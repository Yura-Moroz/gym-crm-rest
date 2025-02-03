package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.converters.traineeConverters.TraineeDtoToTraineeEntityConverter;
import com.yuramoroz.spring_crm_system.converters.traineeConverters.TraineeToDtoConverter;
import com.yuramoroz.spring_crm_system.converters.trainingConverters.TrainingDtoToTrainingEntityConverter;
import com.yuramoroz.spring_crm_system.converters.trainingConverters.TrainingEntityToTrainingDtoConverter;
import com.yuramoroz.spring_crm_system.dto.trainees.*;
import com.yuramoroz.spring_crm_system.dto.UserLoginDto;
import com.yuramoroz.spring_crm_system.dto.trainings.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import com.yuramoroz.spring_crm_system.views.TraineeViews;
import com.yuramoroz.spring_crm_system.views.TrainingViews;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RequestMapping("/gym-api/trainees")
@RestController
@Validated
public class TraineeController {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TraineeDtoToTraineeEntityConverter toTraineeEntityConverter;

    @Autowired
    private TraineeToDtoConverter toTraineeDtoConverter;

    @Autowired
    private TrainingEntityToTrainingDtoConverter toTrainingDtoConverter;

    @Autowired
    private TrainingDtoToTrainingEntityConverter toTrainingEntityConverter;


    @PostMapping
    @JsonView(TraineeViews.Login.class)
    public ResponseEntity<TraineeDto> createProfile(@RequestBody @Valid
                                                    @JsonView(TraineeViews.Input.class) TraineeDto traineeDto) {
        Trainee trainee = toTraineeEntityConverter.convert(traineeDto);
        Trainee createdTrainee = traineeService.save(trainee.getFirstName(), trainee.getLastName(),
                trainee.getPassword(), trainee.getAddress(), trainee.getDateOfBirth());

        return ResponseEntity.status(HttpStatus.CREATED).body(toTraineeDtoConverter.convert(createdTrainee));
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
        TraineeDto profileDto = toTraineeDtoConverter.convert(traineeService.getByUsername(username).get());
        return ResponseEntity.ok(profileDto);
    }


    @PutMapping("/{id}")
    @JsonView(TraineeViews.UpdateResp.class)
    public ResponseEntity<TraineeDto> updateProfile(@PathVariable long id,
                                                    @RequestBody @Valid
                                                    @JsonView(TraineeViews.UpdateReq.class) TraineeDto traineeDto) {
        Trainee trainee = traineeService.getById(id).get();
        trainee.setUserName(traineeDto.getUserName());
        trainee.setFirstName(traineeDto.getFirstName());
        trainee.setLastName(traineeDto.getLastName());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth() == null ? trainee.getDateOfBirth() : traineeDto.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress() == null ? trainee.getAddress() : traineeDto.getAddress());
        trainee.setActive(traineeDto.isActive());

        Trainee updatedTrainee = traineeService.update(trainee);
        return ResponseEntity.status(HttpStatus.OK)
                .body(toTraineeDtoConverter.convert(updatedTrainee));
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
                                                                 @JsonView(TrainingViews.UpdateTraineeTrainings.class)
                                                                 List<TrainingDto> trainingsDto) {
        Trainee trainee = traineeService.getByUsername(username).get();
        trainee.setTrainings(trainingsDto.stream()
                .map(trainingDto -> toTrainingEntityConverter.convert(trainingDto)).collect(Collectors.toList()));

        Trainee updatedTrainee = traineeService.update(trainee);

        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedTrainee.getTrainings()
                        .stream()
                        .map(training -> toTrainingDtoConverter.convert(training))
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
