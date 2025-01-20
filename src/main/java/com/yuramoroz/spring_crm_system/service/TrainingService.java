package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.repository.impl.TrainingDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
@Slf4j
public class TrainingService {

    @Autowired
    private TrainingDao trainingDao;

    public Training saveTraining(Training training) {
        log.info("Trying to save training");
        return trainingDao.save(training);
    }

    public Training selectTrainingById(long id) {
        log.info("Selecting Training by id: " + id);
        if (trainingDao.ifExistById(id)) {
            return trainingDao.getById(id).get();
        } else {
            throw new NoSuchElementException("There is no Training with such id: " + id);
        }
    }

}
