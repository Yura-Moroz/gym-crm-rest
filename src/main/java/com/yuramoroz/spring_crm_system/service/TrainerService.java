package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.impl.TrainerDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TrainerService extends BaseUserService<Trainer> {

    @Autowired
    private final TrainerDao trainerDao;

    public TrainerService(TrainerDao trainerDao) {
        super(trainerDao);
        this.trainerDao = trainerDao;
    }

    public Trainer saveTrainer(String firstName, String lastName, String password, String specialization) {
        log.info("Trying to create and save {} {} trainer...", firstName, lastName);

        Trainer trainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .specialization(specialization)
                .build();

        return super.saveUser(trainer);
    }

    public List<Training> getTrainingsByCriteria(String username, LocalDate dateFrom, LocalDate dateTo,
                                                 String trainerName, TrainingType trainingType) {
        log.info("Trying to get trainer's trainings by criteria and username");

        List<Training> trainings = new ArrayList<>();
        if (trainerDao.ifUserExistByUsername(username)) {
            trainings = trainerDao.getTrainingsByCriteria(username, dateFrom, dateTo, trainerName, trainingType);
        } else {
            log.warn("There was no User found with such a username {}", username);
        }
        return trainings;
    }

    public List<Trainer> getUnassignedTrainersToUserByUsername(String username){
        log.info("Trying to get unassigned trainers to a particular user by username: {}", username);

        List<Trainer> trainers = new ArrayList<>();
        try{
            trainers = trainerDao.getUnassignedTrainers(username);
        }catch (Exception e){
            log.warn("There was something wrong with getting a trainers list...");
        }
        return trainers;
    }

}
