package com.yuramoroz.spring_crm_system.service.impl;

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

    public Trainer save(String firstName, String lastName, String password, String specialization) {
        log.info("Trying to create and save {} {} trainer...", firstName, lastName);

        Trainer trainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .specialization(specialization)
                .build();

        return super.save(trainer);
    }

    public List<Trainer> getUnassignedTrainersToUserByUsername(String username){
        log.info("Trying to get unassigned trainers to a particular user by username: {}", username);
        return repository.getUnassignedTrainers(username);
    }

}
