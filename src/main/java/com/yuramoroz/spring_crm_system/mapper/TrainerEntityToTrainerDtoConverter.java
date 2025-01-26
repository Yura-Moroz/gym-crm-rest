package com.yuramoroz.spring_crm_system.mapper;

import com.yuramoroz.spring_crm_system.dto.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerEntityToTrainerDtoConverter implements Converter<Trainer, TrainerDto> {

    @Override
    public TrainerDto convert(@NotNull Trainer trainer) {
        List<Trainee> traineeList = trainer.getTrainings()
                .stream()
                .map(Training::getTrainee)
                .collect(Collectors.toList());

        return TrainerDto.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .userName(trainer.getUserName())
                .password(trainer.getPassword())
                .active(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .trainingList(trainer.getTrainings())
                .trainees(traineeList)
                .build();
    }
}
