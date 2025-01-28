package com.yuramoroz.spring_crm_system.repository.impl;

import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.repository.TrainerDao;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class TrainerDaoImpl extends UserDaoImpl<Trainer> implements TrainerDao {

    public TrainerDaoImpl() {
        super(Trainer.class);
    }

    @Override
    public List<Trainer> getUnassignedTrainers(String username) {
        log.info("Trying to get unassigned trainers to trainee by username: {}", username);

        String jpql = """
                SELECT t FROM Trainer t WHERE t.id
                NOT IN (SELECT training.trainer.id FROM Training training WHERE training.trainee.userName = :username)
                """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("username", username);

        return query.getResultList();
    }

}
