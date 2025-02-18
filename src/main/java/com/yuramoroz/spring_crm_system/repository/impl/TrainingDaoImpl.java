package com.yuramoroz.spring_crm_system.repository.impl;

import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.repository.TrainingDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingDaoImpl implements TrainingDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Training> getById(long id) {
        log.info("Getting a training by id");

        Training training = entityManager.find(Training.class, id);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> getAll() {
        log.info("Getting a list of all trainings in the DB");

        Query query = entityManager.createQuery("SELECT t FROM Training t");
        return query.getResultList();
    }

    @Override
    @Transactional
    public Training save(Training training) {
        log.info("Trying to save a training to the DB");
        // entityManager.persist() returned type is void
        entityManager.persist(training);
        return training;
    }

    @Override
    public boolean ifExistById(long id) {
        log.info("Checking if user exist by id");
        return entityManager.find(Training.class, id) != null;
    }

    @Override
    public List<Training> getTrainingsByTraineeUsernameAndDateRange(String traineeLogin, LocalDate dateFrom, LocalDate dateTo, String trainerLogin, TrainingType trainingType) {
        log.info("Trying to get trainings by criteria from DB");

        String jpql = """
                SELECT t FROM Training t WHERE t.trainee.userName = :traineeLogin
                AND t.trainingDate >= :dateFrom AND t.trainingDate <= :dateTo
                AND (t.trainer.userName = :trainerLogin)
                AND t.trainingType = :trainingType
                """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("traineeLogin", traineeLogin);
        query.setParameter("dateFrom", dateFrom == null ? LocalDate.now().atTime(0, 0, 0) : dateFrom.atTime(0, 0, 0));
        query.setParameter("dateTo", dateTo == null ? LocalDate.now().atTime(23, 59, 59) : dateTo.atTime(23, 59, 59));
        query.setParameter("trainerLogin", trainerLogin);
        query.setParameter("trainingType", trainingType);

        return query.getResultList();
    }

    @Override
    public List<Training> getTrainingsByTrainerUsernameAndDateRange(String trainerLogin, LocalDate dateFrom, LocalDate dateTo, String traineeLogin, TrainingType trainingType) {
        log.info("Trying to get trainings by criteria from DB");

        String jpql = """
                SELECT t FROM Training t where t.trainer.userName = :trainerLogin
                AND t.trainingDate >= :dateFrom AND t.trainingDate <= :dateTo
                AND t.trainee.userName = :traineeLogin
                AND t.trainingType = :trainingType
                """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("trainerLogin", trainerLogin);
        query.setParameter("dateFrom", dateFrom == null ? LocalDate.now().atTime(0, 0, 0) : dateFrom.atTime(0, 0, 0));
        query.setParameter("dateTo", dateTo == null ? LocalDate.now().atTime(23, 59, 59) : dateTo.atTime(23, 59, 59));
        query.setParameter("traineeLogin", traineeLogin);
        query.setParameter("trainingType", trainingType);

        return query.getResultList();
    }

    @Override
    public long count(){
        log.info("Getting Training entities count from the DB");

        String jpql = "SELECT COUNT(training) FROM Training training";
        return (Long) entityManager.createQuery(jpql).getSingleResult();
    }

    public Training update(Training training) {
        log.info("Updating Training with id: " + training.getId());
        return entityManager.merge(training);
    }

    @Override
    @Transactional
    public void delete(Training training){
        log.info("Tying to delete Training");
        entityManager.remove(training);
    }
}
