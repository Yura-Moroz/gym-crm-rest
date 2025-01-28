package com.yuramoroz.spring_crm_system.repository;

import com.yuramoroz.spring_crm_system.entity.User;

import java.util.Optional;

public interface UserDao<T extends User> extends BaseDao<T> {
    public Optional<T> getByUsername(String username);

    public boolean ifExistByUsername(String username);

    public T update(T entity);

    public void delete(T entity);

}
