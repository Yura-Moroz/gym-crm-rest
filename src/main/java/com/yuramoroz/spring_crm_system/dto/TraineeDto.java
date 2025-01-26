package com.yuramoroz.spring_crm_system.dto;

import com.yuramoroz.spring_crm_system.entity.Training;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeDto extends UserDto{

    private String address;

    private LocalDate dateOfBirth;

    private List<Training> trainings = new ArrayList<>();
}
