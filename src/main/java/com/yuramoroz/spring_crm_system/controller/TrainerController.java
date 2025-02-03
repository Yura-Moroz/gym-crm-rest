package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.converters.trainerConverters.TrainerDtoToTrainerEntityConverter;
import com.yuramoroz.spring_crm_system.converters.trainerConverters.TrainerEntityToTrainerDtoConverter;
import com.yuramoroz.spring_crm_system.dto.UserLoginDto;
import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import com.yuramoroz.spring_crm_system.views.TrainerViews;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/gym-api/trainers")
@RestController
@Validated
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerDtoToTrainerEntityConverter toTrainerEntityConverter;

    @Autowired
    private TrainerEntityToTrainerDtoConverter toTrainerDtoConverter;


    @PostMapping
    @JsonView(TrainerViews.Login.class)
    public ResponseEntity<TrainerDto> createProfile(@RequestBody
                                                    @Valid
                                                    @JsonView(TrainerViews.Input.class) TrainerDto trainerDto) {

        Trainer trainer = toTrainerEntityConverter.convert(trainerDto);
        Trainer createdTrainer = trainerService.save(trainer.getFirstName(), trainer.getLastName(),
                trainer.getPassword(), trainer.getSpecialization());

        return ResponseEntity.status(HttpStatus.CREATED).body(toTrainerDtoConverter.convert(createdTrainer));
    }


    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid UserLoginDto userLoginDto) {
        Trainer trainer = trainerService.getByUsername(userLoginDto.getUserName()).get();
        PasswordChangingResult result = trainerService.changePassword(trainer, userLoginDto.getOldPassword(), userLoginDto.getNewPassword());
        return result.isSucceed() ?
                new ResponseEntity<>(result.getMessage(), HttpStatus.OK) :
                new ResponseEntity<>(result.getMessage(), HttpStatus.BAD_REQUEST);

    }


    @GetMapping("/{username}")
    @JsonView(TrainerViews.GetResp.class)
    public ResponseEntity<TrainerDto> getProfileByUsername(@PathVariable String username) {
        TrainerDto profileDto = toTrainerDtoConverter.convert(trainerService.getByUsername(username).get());
        return ResponseEntity.ok(profileDto);
    }


    @PutMapping("/{id}")
    @JsonView(TrainerViews.UpdatingResp.class)
    public ResponseEntity<TrainerDto> updateProfile(@PathVariable long id,
                                                    @RequestBody
                                                    @JsonView(TrainerViews.UpdatingReq.class)
                                                    @Valid TrainerDto trainerUpdatingDto) {
        Trainer trainer = trainerService.getById(id).get();
        trainer.setFirstName(trainerUpdatingDto.getFirstName());
        trainer.setLastName(trainerUpdatingDto.getLastName());
        trainer.setUserName(trainerUpdatingDto.getUserName());
        trainer.setActive(trainerUpdatingDto.isActive());

        Trainer updatedTrainer = trainerService.update(trainer);
        return ResponseEntity.status(HttpStatus.OK)
                .body(toTrainerDtoConverter.convert(updatedTrainer));

    }


    @GetMapping("/{username}/unassigned")
    @JsonView(TrainerViews.Unassigned.class)
    public ResponseEntity<List<TrainerDto>> getUnassignedTrainersByTraineeUsername(@PathVariable String username) {
        List<Trainer> unassignedTrainers = trainerService.getUnassignedTrainersToUserByUsername(username);
        List<TrainerDto> resultList = unassignedTrainers
                .stream()
                .map(trainer -> toTrainerDtoConverter.convert(trainer))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }


    @PatchMapping("/status")
    public ResponseEntity<Void> changeStatus(@RequestBody
                                             @Valid
                                             @JsonView(TrainerViews.Status.class)
                                             TrainerDto trainerDto) {
        Trainer trainer = trainerService.getByUsername(trainerDto.getUserName()).get();
        if (trainer.isActive()) trainerService.deactivate(trainer);
        else trainerService.activate(trainer);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
