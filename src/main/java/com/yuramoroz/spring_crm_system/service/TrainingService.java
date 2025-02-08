package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

    public Training save(Training training);

    public Optional<Training> getById(long id);

    public List<Training> getAll();

    public List<Training> getTrainingsByTraineeUsernameAndDateRange(
            String username, LocalDate dateFrom, LocalDate dateTo, String trainerName, TrainingType trainingType);

    public List<Training> getTrainingsByTrainerUsernameAndDateRange(
            String username, LocalDate dateFrom, LocalDate dateTo, String trainerName, TrainingType trainingType);

}
