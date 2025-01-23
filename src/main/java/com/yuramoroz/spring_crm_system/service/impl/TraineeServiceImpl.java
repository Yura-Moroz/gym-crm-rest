package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.repository.TraineeDao;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class TraineeServiceImpl extends BaseUserServiceImpl<Trainee, TraineeDao> implements TraineeService {

    public TraineeServiceImpl(TraineeDao repository) {
        super(repository);
    }

    @Override
    public Trainee save(String firstName, String lastName, String password, String address, LocalDate dateOfBirth) {
        log.info("Trying to create and save {} {} trainee...", firstName, lastName);

        Trainee trainee = Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .build();

        return super.save(trainee);
    }

    @Override
    public void deleteByUsername(String username) {
        log.info("Trying to delete a user by {}", username);

        if (repository.ifExistByUsername(username)) {
            Trainee user = repository.getByUsername(username).get();
            repository.delete(user);
        }
    }
}
