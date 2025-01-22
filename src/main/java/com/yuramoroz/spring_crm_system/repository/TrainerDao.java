package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.User;

import java.util.List;

public interface TrainerDao extends UserDao<Trainer> {

    public List<Trainer> getUnassignedTrainers(String username);
}
