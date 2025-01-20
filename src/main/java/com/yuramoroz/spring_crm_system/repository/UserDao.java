package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.entity.User;

import java.util.Optional;

public interface UserDao<T extends User> extends BaseDao<T> {
    public Optional<T> getUserByUsername(String username);

    public boolean ifUserExistByUsername(String username);

    public T update(T entity);

    public void deleteByUsername(String username);

    public void delete(T entity);

}
