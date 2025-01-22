package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDao extends BaseDao<Training> {

    public List<Training> getTrainingsByTraineeUsernameAndDateRange(
            String traineeLogin, LocalDate dateFrom, LocalDate dateTo, String trainerLogin, TrainingType trainingType);

    public List<Training> getTrainingsByTrainerUsernameAndDateRange(
            String trainerLogin, LocalDate dateFrom, LocalDate dateTo, String traineeLogin, TrainingType trainingType);

}
