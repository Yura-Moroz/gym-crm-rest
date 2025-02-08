package com.yuramoroz.spring_crm_system.converters.trainingConverters;

import com.yuramoroz.spring_crm_system.dto.trainings.TrainingAddingDto;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.repository.TraineeDao;
import com.yuramoroz.spring_crm_system.repository.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingAddingDtoToTrainingConverter implements Converter<TrainingAddingDto, Training> {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TraineeDao traineeDao;

    @Override
    public Training convert(TrainingAddingDto trainingAddingDto) {
        return Training.builder()
                .trainee(traineeDao.getByUsername(trainingAddingDto.getTraineeUsername()).get())
                .trainer(trainerDao.getByUsername(trainingAddingDto.getTrainerUsername()).get())
                .trainingName(trainingAddingDto.getTrainingName())
                .trainingDate(trainingAddingDto.getDate())
                .trainingDuration(trainingAddingDto.getDuration())
                .trainingType(trainingAddingDto.getType())
                .build();
    }
}
