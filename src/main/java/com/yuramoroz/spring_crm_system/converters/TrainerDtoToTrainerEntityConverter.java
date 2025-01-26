package com.yuramoroz.spring_crm_system.converters;

import com.yuramoroz.spring_crm_system.dto.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainerDtoToTrainerEntityConverter implements Converter<TrainerDto, Trainer> {

    @Override
    public Trainer convert(TrainerDto trainerDto) {
        return Trainer.builder()
                .id(trainerDto.getId())
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .userName(trainerDto.getUserName())
                .password(trainerDto.getPassword())
                .specialization(trainerDto.getSpecialization())
                .active(trainerDto.isActive())
                .trainings(trainerDto.getTrainingList())
                .build();
    }
}
