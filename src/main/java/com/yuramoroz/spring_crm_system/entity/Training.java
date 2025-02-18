package com.yuramoroz.spring_crm_system.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.enums.TrainingType;
import com.yuramoroz.spring_crm_system.views.TraineeViews;
import com.yuramoroz.spring_crm_system.views.TrainerViews;
import com.yuramoroz.spring_crm_system.views.TrainingViews;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "trainee")
@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({TraineeViews.GetResp.class, TrainerViews.GetResp.class})
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    @JsonView({TraineeViews.GetResp.class, TrainerViews.GetResp.class, TrainingViews.GetResp.class})
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    @JsonView({TraineeViews.GetResp.class, TrainerViews.GetResp.class, TrainingViews.GetResp.class})
    private Trainer trainer;

    @NotNull
    @Column(name = "training_name", nullable = false)
    @JsonView({TraineeViews.GetResp.class, TrainerViews.GetResp.class})
    private String trainingName;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonView({TraineeViews.GetResp.class, TrainerViews.GetResp.class})
    private TrainingType trainingType;

    @NotNull
    @Column(name = "date", nullable = false)
    @JsonView({TraineeViews.GetResp.class, TrainerViews.GetResp.class})
    private LocalDateTime trainingDate;

    @NotNull
    @Column(name = "duration", nullable = false)
    @JsonView({TraineeViews.GetResp.class, TrainerViews.GetResp.class})
    private Duration trainingDuration;
}
