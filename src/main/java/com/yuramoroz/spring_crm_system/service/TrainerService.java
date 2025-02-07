package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.dto.trainers.TrainerDto;
import com.yuramoroz.spring_crm_system.entity.Trainer;

import java.util.List;

public interface TrainerService extends BaseUserService<Trainer>{

    public Trainer save(TrainerDto trainerDto);

    public Trainer update(long id, TrainerDto trainerDto);

    public List<Trainer> getUnassignedTrainersToUserByUsername(String username);

}
