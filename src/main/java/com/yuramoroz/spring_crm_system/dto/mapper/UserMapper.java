package com.yuramoroz.spring_crm_system.dto.mapper;

import com.yuramoroz.spring_crm_system.dto.UserDto;
import com.yuramoroz.spring_crm_system.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper<E extends User, D extends UserDto> {

    @Autowired
    private ModelMapper mapper;

    private D dto;
    private Class<D> dtoClass;

    private E entity;
    private Class<E> entityClass;

    public D toDto(E entity) {
        return mapper.map(entity, dtoClass);
    }

    public E fromDto(D dto){
        return mapper.map(dto, entityClass);
    }

}
