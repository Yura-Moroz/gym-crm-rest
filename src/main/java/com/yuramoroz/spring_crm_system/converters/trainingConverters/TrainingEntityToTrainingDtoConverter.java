package com.yuramoroz.spring_crm_system.converters.trainingConverters;

import com.yuramoroz.spring_crm_system.dto.trainings.TrainingDto;
import com.yuramoroz.spring_crm_system.entity.Training;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingEntityToTrainingDtoConverter implements Converter<Training, TrainingDto> {

    @Override
    public TrainingDto convert(@NotNull Training training) {
        return TrainingDto.builder()
                .id(training.getId())
                .trainee(training.getTrainee())
                .trainer(training.getTrainer())
                .date(training.getTrainingDate())
                .trainingName(training.getTrainingName())
                .duration(training.getTrainingDuration())
                .type(training.getTrainingType())
                .build();
    }
}
