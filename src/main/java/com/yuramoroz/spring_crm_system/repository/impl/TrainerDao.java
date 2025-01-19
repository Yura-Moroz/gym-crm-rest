package com.yuramoroz.spring_crm_system.repository.impl;

import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainerDao extends UserDaoImpl<Trainer> {

    public TrainerDao() {
        super(Trainer.class);
    }

    public List<Training> getTrainingsByCriteria(String username, LocalDate dateFrom, LocalDate dateTo,
                                                 String trainerName, TrainingType trainingType){
        log.info("Trying to get trainings by criteria from DB");

        String jpql = """
               SELECT t FROM Training t where t.trainer.userName = :username
               AND t.trainingDate >= :dateFrom AND t.trainingDate <= :dateTo
               AND t.trainee.firstName = :trainerName OR t.trainee.lastName = :trainerName
               AND t.trainingType = :trainingType
               """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("username", username);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);
        query.setParameter("trainerName", trainerName);
        query.setParameter("trainingType", trainingType);

        List<Training> trainings = query.getResultList();
        return trainings.isEmpty() ? null : trainings;
    }

    public List<Trainer> getUnassignedTrainers(String username){
        log.info("Trying to get unassigned trainers to trainee by username: {}", username);

        String jpql = """
               SELECT t FROM Trainer t WHERE t.id
               NOT IN (SELECT training.trainer.id FROM Training training WHERE training.trainee.userName = :username)
               """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("username", username);

        List<Trainer> trainers = query.getResultList();
        return trainers.isEmpty() ? null : trainers;
    }

}
