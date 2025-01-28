package com.yuramoroz.spring_crm_system.dto;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
public class TrainingDto {

    private Long id;

    private Trainee trainee;

    private Trainer trainer;

    private String trainingName;

    private TrainingType trainingType;

    private LocalDateTime trainingDate;

    private Duration trainingDuration;
}
