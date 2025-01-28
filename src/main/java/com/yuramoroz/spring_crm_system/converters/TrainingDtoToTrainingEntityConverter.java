package com.yuramoroz.spring_crm_system.converters;

import com.yuramoroz.spring_crm_system.dto.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Training;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingDtoToTrainingEntityConverter implements Converter<TrainingDto, Training> {

    @Override
    public Training convert(@NotNull TrainingDto trainingDto) {
        return Training.builder()
                .id(trainingDto.getId())
                .trainee(trainingDto.getTrainee())
                .trainer(trainingDto.getTrainer())
                .trainingDate(trainingDto.getTrainingDate())
                .trainingName(trainingDto.getTrainingName())
                .trainingDuration(trainingDto.getTrainingDuration())
                .trainingType(trainingDto.getTrainingType())
                .build();
    }
}
