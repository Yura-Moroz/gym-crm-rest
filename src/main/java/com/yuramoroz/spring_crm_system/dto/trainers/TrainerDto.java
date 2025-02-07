package com.yuramoroz.spring_crm_system.dto.trainers;

import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.views.TrainerViews;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {

    @JsonView({TrainerViews.Hidden.class})
    private Long id;

    @NotNull
    @JsonView({TrainerViews.Input.class, TrainerViews.GetResp.class,
            TrainerViews.UpdatingReq.class, TrainerViews.Unassigned.class, TrainerViews.Status.class})
    private String firstName;

    @NotNull
    @JsonView({TrainerViews.Input.class, TrainerViews.GetResp.class,
            TrainerViews.UpdatingReq.class, TrainerViews.Unassigned.class, TrainerViews.Status.class})
    private String lastName;

    @JsonView({TrainerViews.Status.class, TrainerViews.Login.class, TrainerViews.UpdatingReq.class, TrainerViews.Unassigned.class})
    private String userName;

    @JsonView({TrainerViews.Input.class, TrainerViews.Login.class})
    private String password;

    @NotNull
    @JsonView({TrainerViews.Status.class, TrainerViews.UpdatingReq.class,
            TrainerViews.GetResp.class, TrainerViews.Status.class})
    private boolean active;

    @NotNull
    @JsonView({TrainerViews.Input.class, TrainerViews.GetResp.class,
            TrainerViews.UpdatingReq.class, TrainerViews.Unassigned.class, TrainerViews.Status.class})
    private String specialization;

    @JsonView({TrainerViews.UpdatingResp.class, TrainerViews.GetResp.class})
    private List<Training> trainings;

    @JsonView({TrainerViews.Hidden.class})
    private List<Trainee> trainees;
}
