package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.User;

import java.util.Optional;

public interface BaseUserService<T extends User> {

    public T save(T user);

    public Optional<T> getByUsername(String username);

    public Optional<T> getById(long id);

    public boolean changePassword(T user, String oldPassword, String newPassword);

    public T update(T user);

    public boolean deactivate(T user);

    public boolean activate(T user);

    public void delete(T user);

}
