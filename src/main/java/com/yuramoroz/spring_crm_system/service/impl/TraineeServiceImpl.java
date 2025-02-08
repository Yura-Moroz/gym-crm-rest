package com.yuramoroz.spring_crm_system.service.impl;

import com.yuramoroz.spring_crm_system.dto.trainees.TraineeDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.exceptionHandling.exceptions.ChangingConstraintViolationException;
import com.yuramoroz.spring_crm_system.exceptionHandling.exceptions.NoSuchEntityPresentException;
import com.yuramoroz.spring_crm_system.repository.TraineeDao;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraineeServiceImpl extends BaseUserServiceImpl<Trainee, TraineeDao> implements TraineeService {

    private final ConversionService conversionService;

    public TraineeServiceImpl(TraineeDao repository, ConversionService conversionService) {
        super(repository);
        this.conversionService = conversionService;
    }

    @Override
    public Trainee save(TraineeDto traineeDto) {
        if (traineeDto == null) throw new IllegalArgumentException("User cannot be null");
        log.info("Trying to create and save {} {} trainee...", traineeDto.getFirstName(), traineeDto.getLastName());

        return super.save(conversionService.convert(traineeDto, Trainee.class));
    }

    @Override
    public Trainee update(long id, TraineeDto traineeUpdatingDto) {
        log.info("Trying to update Trainee profile");

        if (!repository.ifExistById(id)) {
            throw new IllegalArgumentException("There is no trainee with id: " + id);
        }

        Trainee oldTrainee = repository.getById(id).get();
        Trainee updatedTrainee = conversionService.convert(traineeUpdatingDto, Trainee.class);
        updatedTrainee.setId(id);
        updatedTrainee.setPassword(repository.getById(id).get().getPassword());

        if (!oldTrainee.getUserName().equals(updatedTrainee.getUserName())) {
            throw new ChangingConstraintViolationException("The username cannot be changed!");
        }

        return repository.update(updatedTrainee);
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

        if (trainee == null || trainings.isEmpty()) {
            throw new IllegalArgumentException("Cannot update Trainee trainings. Arguments can't be null");
        }

        if (!repository.ifExistById(trainee.getId())) {
            throw new NoSuchEntityPresentException("There is no user in DB with such id: " + trainee.getId());
        }

        trainee.setTrainings(trainings
                .stream()
                .filter(training -> training.getTrainee().getId().equals(trainee.getId()))
                .collect(Collectors.toList()));

        return repository.update(trainee);
    }
}
