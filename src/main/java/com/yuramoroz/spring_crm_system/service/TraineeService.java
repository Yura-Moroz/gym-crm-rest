package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Trainee;

import java.time.LocalDate;

public interface TraineeService extends BaseUserService<Trainee> {

    public Trainee save(String firstName, String lastName, String password, String address, LocalDate dateOfBirth);

    public void deleteByUsername(String username);

}
