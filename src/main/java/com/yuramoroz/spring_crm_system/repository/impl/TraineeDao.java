package com.yuramoroz.spring_crm_system.repository.impl;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TraineeDao extends UserDaoImpl<Trainee> {

    public TraineeDao() {
        super(Trainee.class);
    }

    public List<Training> getTrainingsByCriteria(String username, LocalDate dateFrom, LocalDate dateTo,
                                                 String trainerName, TrainingType trainingType){
        log.info("Trying to get trainings by criteria from DB");

        String jpql = """
               SELECT t FROM Training t WHERE t.trainee.userName = :username
               AND t.trainingDate >= :dateFrom AND t.trainingDate <= :dateTo
               AND (t.trainer.firstName = :trainerName OR t.trainer.lastName = :trainerName)
               AND t.trainingType = :trainingType
               """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("username", username);
        query.setParameter("dateFrom", dateFrom.atTime(0, 0, 0));
        query.setParameter("dateTo", dateTo.atTime(23, 59, 59));
        query.setParameter("trainerName", trainerName);
        query.setParameter("trainingType", trainingType);

        List<Training> trainings = query.getResultList();
        return trainings.isEmpty() ? new ArrayList<>() : trainings;
    }

}
