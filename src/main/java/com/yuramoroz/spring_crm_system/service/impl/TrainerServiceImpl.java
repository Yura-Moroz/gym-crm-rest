package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.repository.TrainerDao;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainerServiceImpl extends BaseUserServiceImpl<Trainer, TrainerDao> implements TrainerService {

    public TrainerServiceImpl(TrainerDao repository) {
        super(repository);
    }

    @Override
    public Trainer save(TrainerDto trainerDto) {
        if (trainerDto == null) throw new IllegalArgumentException("The user cannot be null");
        log.info("Trying to create and save {} {} trainer...", trainerDto.getFirstName(), trainerDto.getLastName());

        Trainer trainer = Trainer.builder()
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .password(trainerDto.getPassword())
                .specialization(trainerDto.getSpecialization())
                .build();

        return super.save(trainer);
    }

    @Override
    public Trainer update(Trainer trainer, TrainerDto trainerUpdatingDto) {
        log.info("Trying to update Trainer profile");
        if (trainerUpdatingDto == null || trainer == null) throw new IllegalArgumentException("Can't update the user with null entity");

        if(repository.ifExistById(trainer.getId())) {
            trainer.setFirstName(trainerUpdatingDto.getFirstName());
            trainer.setLastName(trainerUpdatingDto.getLastName());
            trainer.setUserName(trainerUpdatingDto.getUserName());
            trainer.setActive(trainerUpdatingDto.isActive());

            return repository.update(trainer);
        }
        throw new IllegalArgumentException("There is no user with id: " + trainer.getId());
    }

    @Override
    public List<Trainer> getUnassignedTrainersToUserByUsername(String username) {
        log.info("Trying to get unassigned trainers to a particular user by username: {}", username);
        return repository.getUnassignedTrainers(username);
    }

}
