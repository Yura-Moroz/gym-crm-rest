package com.yuramoroz.spring_crm_system.repository.impl;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.repository.TraineeDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TraineeDaoImpl extends UserDaoImpl<Trainee> implements TraineeDao {

    public TraineeDaoImpl() {
        super(Trainee.class);
    }

}
