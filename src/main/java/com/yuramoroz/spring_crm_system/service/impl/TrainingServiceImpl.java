package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.TrainingDao;
import com.yuramoroz.spring_crm_system.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;

    @Override
    public Training save(Training training) {
        log.info("Trying to save training");
        return trainingDao.save(training);
    }

    @Override
    public Optional<Training> getById(long id) {
        log.info("Selecting Training by id: " + id);
        return trainingDao.getById(id);
    }

    public List<Training> getAll(){
        log.info("Selecting all trainings from the DB");
        return trainingDao.getAll();
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

    @Override
    public long count(){
        log.info("Retrieving trainings count");
        return trainingDao.count();
    }

}
