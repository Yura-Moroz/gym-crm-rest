package com.yuramoroz.spring_crm_system.mapper;

import com.yuramoroz.spring_crm_system.dto.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.repository.TrainingDao;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TrainerMapper {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private ModelMapper mapper;

    public TrainerDto toDto(Trainer trainer) {

        return TrainerDto.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .userName(trainer.getUserName())
                .password(trainer.getPassword())
                .active(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .trainingList(trainingDao.getAll()
                        .stream()
                        .filter(training -> training.getTrainer().getId() == trainer.getId()).collect(Collectors.toList()))
                .build();
    }

    public Trainer fromDto(TrainerDto trainerDto){
        return mapper.map(trainerDto, Trainer.class);
    }
}
