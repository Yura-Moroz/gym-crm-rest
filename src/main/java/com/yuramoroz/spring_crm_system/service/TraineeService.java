package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.impl.TraineeDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TraineeService extends BaseUserService<Trainee>{

    @Autowired
    private final TraineeDao traineeDao;

    public TraineeService(TraineeDao traineeDao) {
        super(traineeDao);
        this.traineeDao = traineeDao;
    }

    public Trainee saveTrainee(String firstName, String lastName, String password, String address, LocalDate dateOfBirth) {
        log.info("Trying to create and save {} {} trainee...", firstName, lastName);

        Trainee trainee = Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .build();

        return super.saveUser(trainee);
    }

    public List<Training> getTrainingsByCriteria(String username, LocalDate dateFrom, LocalDate dateTo,
                                                 String trainerName, TrainingType trainingType){
        log.info("Trying to get trainee's trainings by criteria and username");

        List<Training> trainings = new ArrayList<>();
        if(traineeDao.ifUserExistByUsername(username)){
            trainings = traineeDao.getTrainingsByCriteria(username, dateFrom, dateTo, trainerName, trainingType);
        }else{
            log.warn("There was no user found with such a username {}", username);
        }
        return trainings;
    }

    public void deleteUserByUsername(String username) {
        log.info("Trying to delete a user by {}", username);

        if (traineeDao.ifUserExistByUsername(username)) {
            Trainee user = traineeDao.getUserByUsername(username).get();
            traineeDao.delete(user);
        } else {
            log.warn("There was no user found with {} username", username);
        }
    }
}
