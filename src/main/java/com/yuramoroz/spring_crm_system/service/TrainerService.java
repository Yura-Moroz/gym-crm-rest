package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Trainer;

import java.util.List;

public interface TrainerService extends BaseUserService<Trainer>{

    public Trainer save(String firstName, String lastName, String password, String specialization);

    public List<Trainer> getUnassignedTrainersToUserByUsername(String username);

}
