package com.yuramoroz.spring_crm_system.entity;

import com.yuramoroz.spring_crm_system.enums.TrainingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @NotNull
    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainingType trainingType;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime trainingDate;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Duration trainingDuration;

}
