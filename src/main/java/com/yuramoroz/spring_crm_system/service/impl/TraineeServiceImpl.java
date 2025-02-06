package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.dto.trainees.TraineeDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.repository.TraineeDao;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraineeServiceImpl extends BaseUserServiceImpl<Trainee, TraineeDao> implements TraineeService {

    public TraineeServiceImpl(TraineeDao repository) {
        super(repository);
    }

    @Override
    public Trainee save(TraineeDto traineeDto) {
        if (traineeDto == null) throw new IllegalArgumentException("User cannot be null");
        log.info("Trying to create and save {} {} trainee...", traineeDto.getFirstName(), traineeDto.getLastName());

        Trainee trainee = Trainee.builder()
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .password(traineeDto.getPassword())
                .address(traineeDto.getAddress())
                .dateOfBirth(traineeDto.getDateOfBirth())
                .build();

        return super.save(trainee);
    }

    @Override
    public Trainee update(Trainee trainee, TraineeDto traineeUpdatingDto) {
        log.info("Trying to update Trainee profile");
        if (trainee == null || traineeUpdatingDto == null)
            throw new NoSuchElementException("This user was not found in DB");

        if (repository.ifExistById(trainee.getId())) {
            trainee.setUserName(traineeUpdatingDto.getUserName());
            trainee.setFirstName(traineeUpdatingDto.getFirstName());
            trainee.setLastName(traineeUpdatingDto.getLastName());
            trainee.setDateOfBirth(traineeUpdatingDto.getDateOfBirth() == null ? trainee.getDateOfBirth() : traineeUpdatingDto.getDateOfBirth());
            trainee.setAddress(traineeUpdatingDto.getAddress() == null ? trainee.getAddress() : traineeUpdatingDto.getAddress());
            trainee.setActive(traineeUpdatingDto.isActive());

            return repository.update(trainee);
        }
        throw new IllegalArgumentException("There is no user with id: " + trainee.getId());
    }

    @Override
    public void deleteByUsername(String username) {
        log.info("Trying to delete a user by {}", username);

        if (repository.ifExistByUsername(username)) {
            Trainee user = repository.getByUsername(username).get();
            repository.delete(user);
        }
    }

    @Override
    public Trainee updateTrainings(Trainee trainee, List<Training> trainings) {
        log.info("Trying to update trainings for Trainee");
        if (trainee != null && !trainings.isEmpty()) {
            if (repository.ifExistById(trainee.getId())) {
                trainee.setTrainings(trainings
                        .stream()
                        .filter(training -> Objects.equals(training.getTrainee().getId(), trainee.getId()))
                        .collect(Collectors.toList()));
                return repository.update(trainee);
            }
        }
        throw new IllegalArgumentException("Cannot update Trainee trainings. Arguments can't be null");
    }
}
