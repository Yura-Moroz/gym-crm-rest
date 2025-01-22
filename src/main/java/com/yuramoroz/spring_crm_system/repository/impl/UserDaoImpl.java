package com.yuramoroz.spring_crm_system.repository.impl;

import com.yuramoroz.spring_crm_system.entity.User;
import com.yuramoroz.spring_crm_system.repository.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class UserDaoImpl<T extends User> implements UserDao<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> clazz;

    public UserDaoImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Optional<T> getById(long id) {
        log.info("Getting user by id");

        T entity = entityManager.find(clazz, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<T> getAll() {
        log.info("Getting a list of all users present in the DB");

        Query query = entityManager.createQuery("SELECT user FROM " + clazz.getName() + " user");
        return query.getResultList();
    }

    @Override
    public boolean ifExistById(long id) {
        log.info("Checking if user exist by id");
        return entityManager.find(clazz, id) != null;
    }

    @Override
    @Transactional
    public T save(T entity) {
        log.info("Trying to save an entity to the DB");
        //entityManager.persist() returned type is void
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        log.info("Trying to update an entity in the DB");
        return entityManager.merge(entity);
    }

    @Override
    public void delete(T entity) {
        log.info("Trying to delete a user from the DB");
        entityManager.remove(entity);
    }

    @Override
    public Optional<T> getByUsername(String username) {
        log.info("Trying to get user by '" + username + "' login");

        String jpqlQuery = "SELECT user FROM " + clazz.getName() + " user WHERE user.userName = :login";
        Query query = entityManager.createQuery(jpqlQuery);
        query.setParameter("login", username);

        T returnedUser = (T) query.getSingleResult();

        return Optional.ofNullable(returnedUser);
    }

    @Override
    public boolean ifExistByUsername(String username) {
        log.info("Checking if user exists with '" + username + "' login");

        String jpqlQuery = "SELECT COUNT(user) FROM " + clazz.getName() + " user WHERE user.userName = :login";
        Query query = entityManager.createQuery(jpqlQuery);
        query.setParameter("login", username);
        Long counter = (Long) query.getSingleResult();

        return counter > 0;
    }

}
