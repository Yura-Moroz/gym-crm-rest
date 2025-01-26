package com.yuramoroz.spring_crm_system.converters;

import com.yuramoroz.spring_crm_system.dto.TraineeDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TraineeDtoToTraineeEntityConverter implements Converter<TraineeDto, Trainee> {

    @Override
    public Trainee convert(@NotNull TraineeDto traineeDto) {
        return Trainee.builder()
                .id(traineeDto.getId())
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .address(traineeDto.getAddress())
                .active(traineeDto.isActive())
                .userName(traineeDto.getUserName())
                .password(traineeDto.getPassword())
                .dateOfBirth(traineeDto.getDateOfBirth())
                .trainings(traineeDto.getTrainings())
                .build();
    }
}
