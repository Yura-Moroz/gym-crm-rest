package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.TrainingDao;
import com.yuramoroz.spring_crm_system.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training save(Training training) {
        log.info("Trying to save training");
        return trainingDao.save(training);
    }

    @Override
    public Optional<Training> selectById(long id) {
        log.info("Selecting Training by id: " + id);
        return trainingDao.getById(id);
    }

    @Override
    public List<Training> getTrainingsByTraineeUsernameAndDateRange(String traineeLogin, LocalDate dateFrom, LocalDate dateTo, String trainerLogin, TrainingType trainingType) {
        log.info("Trying to get Trainings list where Trainee username is: {}", traineeLogin);
        return trainingDao.getTrainingsByTraineeUsernameAndDateRange(traineeLogin, dateFrom, dateTo, trainerLogin, trainingType);
    }

    @Override
    public List<Training> getTrainingsByTrainerUsernameAndDateRange(String trainerLogin, LocalDate dateFrom, LocalDate dateTo, String traineeLogin, TrainingType trainingType) {
        log.info("Trying to get Trainings list where Trainer username is: {}", trainerLogin);
        return trainingDao.getTrainingsByTrainerUsernameAndDateRange(trainerLogin, dateFrom, dateTo, traineeLogin, trainingType);
    }

}
