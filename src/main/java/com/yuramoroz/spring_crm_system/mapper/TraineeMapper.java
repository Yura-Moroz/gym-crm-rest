package com.yuramoroz.spring_crm_system.mapper;

import com.yuramoroz.spring_crm_system.dto.TraineeDto;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    @Autowired
    private ModelMapper mapper;

    public TraineeDto toDto(Trainee entity) {
        return mapper.map(entity, TraineeDto.class);
    }

    public Trainee fromDto(TraineeDto traineeDto){
        return mapper.map(traineeDto, Trainee.class);
    }

}
