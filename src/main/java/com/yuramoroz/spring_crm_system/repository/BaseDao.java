package com.yuramoroz.spring_crm_system.repository;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T> {

    public Optional<T> getById(long id);

    public List<T> getAll();

    public T save(T entity);

    public T update(T entity);

    public void delete(T entity);

    public boolean ifExistById(long id);

}
