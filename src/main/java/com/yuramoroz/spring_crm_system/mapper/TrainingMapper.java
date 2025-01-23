package com.yuramoroz.spring_crm_system.mapper;

import com.yuramoroz.spring_crm_system.dto.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Training;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingMapper {

    @Autowired
    private ModelMapper mapper;

    private TrainingDto toDto(Training training) {
        return mapper.map(training, TrainingDto.class);
    }

    private Training fromDto(TrainingDto trainingDto) {
        return mapper.map(trainingDto, Training.class);
    }

    private List<TrainingDto> toDtoList(List<Training> trainings) {
        return trainings.stream().map(this::toDto).collect(Collectors.toList());
    }

    private List<Training> fromDtoList(List<TrainingDto> trainingDtos) {
        return trainingDtos.stream().map(this::fromDto).collect(Collectors.toList());
    }
}
