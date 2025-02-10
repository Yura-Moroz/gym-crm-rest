package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.exceptionHandling.exceptions.ChangingConstraintViolationException;
import com.yuramoroz.spring_crm_system.exceptionHandling.exceptions.NoSuchEntityPresentException;
import com.yuramoroz.spring_crm_system.repository.TrainerDao;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainerServiceImpl extends BaseUserServiceImpl<Trainer, TrainerDao> implements TrainerService {

    private final ConversionService conversionService;

    public TrainerServiceImpl(TrainerDao repository, ConversionService conversionService) {
        super(repository);
        this.conversionService = conversionService;
    }

    @Override
    public Trainer save(TrainerDto trainerDto) {
        if (trainerDto == null) throw new IllegalArgumentException("The user cannot be null");
        log.info("Trying to create and save {} {} trainer...", trainerDto.getFirstName(), trainerDto.getLastName());

        return super.save(conversionService.convert(trainerDto, Trainer.class));
    }

    @Override
    public Trainer update(long id, TrainerDto trainerUpdatingDto) {
        log.info("Trying to update Trainer profile");

        if (!repository.ifExistById(id)) {
            throw new NoSuchEntityPresentException("There is no trainer with id: " + id);
        }

        Trainer oldTrainer = repository.getById(id).get();
        Trainer updatedTrainer = conversionService.convert(trainerUpdatingDto, Trainer.class);
        updatedTrainer.setId(id);
        updatedTrainer.setPassword(repository.getById(id).get().getPassword());

        if (!oldTrainer.getUserName().equals(updatedTrainer.getUserName())) {
            throw new ChangingConstraintViolationException("The username cannot be changed! ");
        }

        return repository.update(updatedTrainer);

    }

    @Override
    public List<Trainer> getUnassignedTrainersToUserByUsername(String username) {
        log.info("Trying to get unassigned trainers to a particular user by username: {}", username);
        return repository.getUnassignedTrainers(username);
    }

}
