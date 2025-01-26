package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.entity.User;
import com.yuramoroz.spring_crm_system.model.PasswordChangingResult;
import com.yuramoroz.spring_crm_system.repository.UserDao;
import com.yuramoroz.spring_crm_system.service.BaseUserService;
import com.yuramoroz.spring_crm_system.utils.ProfileUtils;
import com.yuramoroz.spring_crm_system.validation.PasswordManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public abstract class BaseUserServiceImpl<T extends User, R extends UserDao<T>> implements BaseUserService<T> {

    protected final R repository;

    @Override
    @Transactional
    public T save(T user) {
        log.info("Trying to save user...");

        if (user == null) throw new IllegalArgumentException("Expected User but no proper data was provided");

        user.setUserName(ProfileUtils.generateUsername(user, repository::ifExistByUsername));
        user.setPassword(PasswordManager.hashPassword(user.getPassword()));

        return repository.save(user);
    }

    @Override
    public Optional<T> getByUsername(String username) {
        log.info("Selecting User by {} username", username);
        return repository.getByUsername(username);
    }

    @Override
    public Optional<T> getById(long id) {
        log.info("Selecting User by id: {}", id);
        return repository.getById(id);
    }

    @Override
    @Transactional
    public PasswordChangingResult changePassword(T user, String oldPassword, String newPassword) {

        if (user == null) {
            return PasswordChangingResult.builder()
                    .succeed(false)
                    .message("Can't change password when user is null")
                    .build();
        }

        boolean succeed = false;
        String resultMessage;

        if (!PasswordManager.ifPasswordMatches(oldPassword, user.getPassword())) {
            resultMessage = "Sorry, It seems that you provided wrong old password";
        } else if (!PasswordManager.verify(newPassword)) {
            resultMessage = "Please check that your new password meets all requirements (length should be 4-10 chars)";
        } else if (!repository.ifExistById(user.getId())) {
            resultMessage = "Sorry, can't change password because provided user doesn't exist...";
        } else {
            user.setPassword(PasswordManager.hashPassword(newPassword));
            update(user);
            resultMessage = "New password successfully set to user";
            succeed = true;
        }

        return PasswordChangingResult.builder()
                .succeed(succeed)
                .message(resultMessage)
                .build();
    }

    @Override
    @Transactional
    public T update(T user) {
        log.info("Updating user");
        if (repository.ifExistById(user.getId())) {
            return repository.update(user);
        } else {
            throw new NoSuchElementException("This user was not found in DB");
        }
    }

    @Override
    public boolean activate(T user) {
        log.info("Activating user profile");
        if(user == null) return false;

        user.setActive(true);
        return repository.update(user).isActive();
    }

    @Override
    public boolean deactivate(T user) {
        log.info("Deactivating user profile");
        if (user == null) return false;

        user.setActive(false);
        return !repository.update(user).isActive();
    }

    @Override
    public void delete(T user) {
        log.info("Deleting user...");
        repository.delete(user);
    }

}
