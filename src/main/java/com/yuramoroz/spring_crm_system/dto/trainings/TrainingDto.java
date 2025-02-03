package com.yuramoroz.spring_crm_system.dto.trainings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.views.TrainingViews;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDto {

    @JsonView(TrainingViews.Hidden.class)
    private Long id;

    @JsonView(TrainingViews.GetTrainerResp.class)
    private Trainee trainee;

    @JsonView(TrainingViews.GetTraineeResp.class)
    private Trainer trainer;

    @JsonView({TrainingViews.UpdateTraineeTrainings.class, TrainingViews.GetResp.class})
    private String trainingName;

    @JsonView({TrainingViews.UpdateTraineeTrainings.class, TrainingViews.GetResp.class})
    private TrainingType type;

    @JsonView({TrainingViews.UpdateTraineeTrainings.class, TrainingViews.GetResp.class})
    private LocalDateTime date;

    @JsonView({TrainingViews.UpdateTraineeTrainings.class, TrainingViews.GetReq.class})
    private Duration duration;
}
