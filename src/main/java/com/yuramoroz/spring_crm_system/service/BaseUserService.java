package com.yuramoroz.spring_crm_system.service;

import com.yuramoroz.spring_crm_system.entity.User;

public interface BaseUserService<T extends User> {

    public T save(T user);

    public User selectByUsername(String username);

    public User selectById(long id);

    public void changePassword(T user, String oldPassword, String newPassword);

    public T update(T user);

    public void deactivate(T user);

    public void activate(T user);

    public void delete(T user);

}
