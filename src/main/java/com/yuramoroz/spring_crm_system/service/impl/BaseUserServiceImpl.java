package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.entity.User;
import com.yuramoroz.spring_crm_system.repository.UserDao;
import com.yuramoroz.spring_crm_system.service.BaseUserService;
import com.yuramoroz.spring_crm_system.utils.ProfileUtils;
import com.yuramoroz.spring_crm_system.validation.PasswordManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@AllArgsConstructor
public abstract class BaseUserServiceImpl<T extends User, R extends UserDao<T>> implements BaseUserService<T> {

    protected final R repository;

    @Transactional
    public T save(T user) {
        log.info("Trying to save user...");

        if (user == null) throw new IllegalArgumentException("Expected User but no proper data was provided");

        user.setUserName(ProfileUtils.generateUsername(user, repository::ifExistByUsername));
        user.setPassword(PasswordManager.hashPassword(user.getPassword()));

        return repository.save(user);
    }

    public User selectByUsername(String username) {
        log.info("Selecting User by {} username", username);

        if (repository.ifExistByUsername(username)) {
            return repository.getByUsername(username).get();
        } else {
            throw new NoSuchElementException("There was no User found with such username: " + username);
        }
    }

    public User selectById(long id) {
        log.info("Selecting User by id: {}", id);

        if (repository.ifExistById(id)) {
            return repository.getById(id).get();
        } else {
            throw new NoSuchElementException("There was no User found with such id: " + id);
        }
    }

    @Transactional
    public void changePassword(T user, String oldPassword, String newPassword) {
        log.info("Trying to change password in user");

        boolean approvedPass = PasswordManager.ifPasswordMatches(oldPassword, user.getPassword());

        if (approvedPass && PasswordManager.verify(newPassword) && repository.ifExistById(user.getId())) {

            user.setPassword(PasswordManager.hashPassword(newPassword));
            update(user);

            log.info("The new password was successfully set to user");

        } else log.warn("Sorry... It seems that you've provided a wrong password...");
    }

    public T update(T user) {
        log.info("Updating user");
        if (repository.ifExistById(user.getId())) {
            return repository.update(user);
        } else {
            throw new NoSuchElementException("This user was not found in DB");
        }
    }

    public void deactivate(T user) {
        log.info("Deactivating user profile");

        user.setActive(false);
    }

    public void activate(T user) {
        log.info("Activating user profile");

        user.setActive(true);
    }

    public void delete(T user) {
        log.info("Deleting user...");
        repository.delete(user);
    }

}
