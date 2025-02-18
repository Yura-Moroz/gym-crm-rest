package com.yuramoroz.spring_crm_system.dto.trainings;

import com.yuramoroz.spring_crm_system.enums.TrainingType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingAddingDto {

    private Long id;

    @NotNull
    private long traineeId;

    @NotNull
    private long trainerId;

    @NotNull
    private String trainingName;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Duration duration;

    @NotNull
    private TrainingType type;

}
