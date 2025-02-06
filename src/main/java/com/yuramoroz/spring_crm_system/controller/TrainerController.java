package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.dto.UserLoginDto;
import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import com.yuramoroz.spring_crm_system.views.TrainerViews;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/gym-api/trainers")
@RestController
@Validated
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    private final ConversionService conversionService;


    @PostMapping
    @JsonView(TrainerViews.Login.class)
    public ResponseEntity<TrainerDto> createProfile(@RequestBody
                                                    @Valid
                                                    @JsonView(TrainerViews.Input.class) TrainerDto trainerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(conversionService.convert(trainerService.save(trainerDto), TrainerDto.class));
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
        TrainerDto profileDto = conversionService.convert(trainerService.getByUsername(username).get(), TrainerDto.class);
        return ResponseEntity.ok(profileDto);
    }


    @PutMapping("/{id}")
    @JsonView(TrainerViews.UpdatingResp.class)
    public ResponseEntity<TrainerDto> updateProfile(@PathVariable long id,
                                                    @RequestBody
                                                    @JsonView(TrainerViews.UpdatingReq.class)
                                                    @Valid TrainerDto trainerUpdatingDto) {
        Trainer trainer = trainerService.getById(id).get();
        return ResponseEntity.status(HttpStatus.OK)
                .body(conversionService.convert(trainerService.update(trainer, trainerUpdatingDto), TrainerDto.class));

    }


    @GetMapping("/{username}/unassigned")
    @JsonView(TrainerViews.Unassigned.class)
    public ResponseEntity<List<TrainerDto>> getUnassignedTrainersByTraineeUsername(@PathVariable String username) {
        List<Trainer> unassignedTrainers = trainerService.getUnassignedTrainersToUserByUsername(username);
        List<TrainerDto> resultList = unassignedTrainers
                .stream()
                .map(trainer -> conversionService.convert(trainer, TrainerDto.class))
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
