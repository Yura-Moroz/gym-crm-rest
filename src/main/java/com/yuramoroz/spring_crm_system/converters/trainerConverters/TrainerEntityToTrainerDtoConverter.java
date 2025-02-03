package com.yuramoroz.spring_crm_system.converters.trainerConverters;

import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerEntityToTrainerDtoConverter implements Converter<Trainer, TrainerDto> {

    @Override
    public TrainerDto convert(@NotNull Trainer trainer) {
        List<Trainee> traineeList = trainer.getTrainings() == null ? new ArrayList<>() :
                trainer.getTrainings().stream()
                        .distinct()
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
                .trainings(trainer.getTrainings())
                .trainees(traineeList)
                .build();
    }
}
