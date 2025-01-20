package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.User;
import com.yuramoroz.spring_crm_system.repository.impl.UserDaoImpl;
import com.yuramoroz.spring_crm_system.utils.ProfileUtils;
import com.yuramoroz.spring_crm_system.validation.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
public abstract class BaseUserService<T extends User> {

    private final UserDaoImpl<T> userDao;

    public BaseUserService(UserDaoImpl<T> userDao) {
        this.userDao = userDao;
    }

    public T saveUser(T user) {
        log.info("Trying to save user...");

        if (user == null) throw new IllegalArgumentException("Expected User but no proper data was provided");

        user.setUserName(ProfileUtils.generateUsername(user, userDao::ifUserExistByUsername));
        user.setPassword(PasswordValidator.hashPassword(user.getPassword()));

        return userDao.save(user);
    }

    public User selectUserByUsername(String username) {
        log.info("Selecting User by {} username", username);

        if (userDao.ifUserExistByUsername(username)) {
            return userDao.getUserByUsername(username).get();
        } else {
            throw new NoSuchElementException("There was no User found with such username: " + username);
        }
    }

    public User selectUserById(long id) {
        log.info("Selecting User by id: {}", id);

        if (userDao.ifExistById(id)) {
            return userDao.getById(id).get();
        } else {
            throw new NoSuchElementException("There was no User found with such id: " + id);
        }
    }

    public void changeUserPassword(T user, String oldPassword, String newPassword) {
        log.info("Trying to change password in user");

        boolean approvedPass = PasswordValidator.ifPasswordMatches(oldPassword, user.getPassword());

        if (approvedPass && PasswordValidator.verify(newPassword) && userDao.ifExistById(user.getId())) {

            user.setPassword(PasswordValidator.hashPassword(newPassword));
            updateUser(user);

            log.info("The new password was successfully set to user");

        } else log.warn("Sorry... It seems that you've provided a wrong password...");
    }

    public T updateUser(T user) {
        log.info("Updating user");
        if (userDao.ifExistById(user.getId())) {
            return userDao.update(user);
        } else {
            throw new NoSuchElementException("This user was not found in DB");
        }
    }

    public void deactivateUser(T user) {
        log.info("Deactivating user profile");

        user.setActive(false);
    }

    public void activateUser(T user) {
        log.info("Activating user profile");

        user.setActive(true);
    }

    public void deleteUser(T user) {
        log.info("Deleting user...");
        userDao.delete(user);
    }

}
