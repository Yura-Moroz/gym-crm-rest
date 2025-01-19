package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.User;
import com.yuramoroz.spring_crm_system.repository.impl.UserDaoImpl;
import com.yuramoroz.spring_crm_system.utils.ProfileUtils;
import com.yuramoroz.spring_crm_system.validation.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
@AllArgsConstructor
public abstract class BaseUserService<T extends User> {

    private final UserDaoImpl<T> userDao;

    public T saveUser(T user) {
        log.info("Trying to save {} {} user...", user.getFirstName(), user.getLastName());

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
        log.info("Trying to change password in {} {} user", user.getFirstName(), user.getLastName());

        boolean approvedPass = PasswordValidator.ifPasswordMatches(oldPassword, user.getPassword());

        if (approvedPass && PasswordValidator.verify(newPassword) && userDao.ifExistById(user.getId())) {

            user.setPassword(PasswordValidator.hashPassword(newPassword));
            updateUser(user);

            log.info("The new password was successfully set to {} {} user", user.getFirstName(), user.getLastName());

        } else log.warn("Sorry... It seems that you've provided a wrong password...");
    }

    public T updateUser(T user) {
        log.info("Updating {} {} user", user.getFirstName(), user.getLastName());
        if (userDao.ifExistById(user.getId())) {
            return userDao.update(user);
        } else {
            throw new NoSuchElementException("This user was not found in DB");
        }
    }

    public void deactivateUser(T user) {
        log.info("Deactivating {} {} profile", user.getFirstName(), user.getLastName());

        user.setActive(false);
    }

    public void activateUser(T user) {
        log.info("Activating {} {} profile", user.getFirstName(), user.getLastName());

        user.setActive(true);
    }

    public void deleteUser(T user) {
        log.info("Deleting user...");
        userDao.delete(user);
    }

}
