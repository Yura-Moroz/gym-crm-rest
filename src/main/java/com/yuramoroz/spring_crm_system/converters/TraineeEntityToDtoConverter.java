package com.yuramoroz.spring_crm_system.converters;

import com.yuramoroz.spring_crm_system.dto.TraineeDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TraineeEntityToDtoConverter implements Converter<Trainee, TraineeDto> {

    @Override
    public TraineeDto convert(@NotNull Trainee trainee) {
        return TraineeDto.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .address(trainee.getAddress())
                .active(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .userName(trainee.getUserName())
                .password(trainee.getPassword())
                .trainings(trainee.getTrainings())
                .build();
    }
}
