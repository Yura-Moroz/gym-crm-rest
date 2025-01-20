package com.yuramoroz.spring_crm_system.repository.impl;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.BaseDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingDao implements BaseDao<Training> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Training> getById(long id) {
        log.info("Getting a training by id");

        Training training = entityManager.find(Training.class, id);
        return training != null ? Optional.of(training) : Optional.empty();
    }

    @Override
    public List<Training> getAll() {
        log.info("Getting a list of all trainings in the DB");

        Query query = entityManager.createQuery("SELECT t FROM Training t");
        List<Training> trainings = query.getResultList();

        return !trainings.isEmpty() ? trainings : new ArrayList<>();
    }

    @Override
    @Transactional
    public Training save(Training training) {
        log.info("Trying to save a training to the DB");
        entityManager.persist(training);
        return training;
    }

    @Override
    public boolean ifExistById(long id) {
        log.info("Checking if user exist by id");
        return entityManager.find(Training.class, id) != null;
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
