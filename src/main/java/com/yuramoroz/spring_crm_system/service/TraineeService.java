package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.dto.trainees.TraineeDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService extends BaseUserService<Trainee> {

    public Trainee save(TraineeDto traineeDto);

    public Trainee update(long id, TraineeDto traineeDto);

    public void deleteByUsername(String username);

    public Trainee updateTrainings(Trainee trainee, List<Training> trainings);

}
