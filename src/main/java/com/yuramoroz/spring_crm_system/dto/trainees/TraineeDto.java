package com.yuramoroz.spring_crm_system.dto.trainees;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.yuramoroz.spring_crm_system.entity.Training;
import com.yuramoroz.spring_crm_system.views.TraineeViews;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeDto {

    @JsonView(TraineeViews.Hidden.class)
    private Long id;

    @JsonView({TraineeViews.Input.class, TraineeViews.UpdateReq.class, TraineeViews.GetResp.class})
    private String firstName;

    @JsonView({TraineeViews.Input.class, TraineeViews.UpdateReq.class, TraineeViews.GetResp.class})
    private String lastName;

    @JsonView({TraineeViews.Login.class, TraineeViews.UpdateReq.class, TraineeViews.Status.class})
    private String userName;

    @JsonView({TraineeViews.Input.class, TraineeViews.Login.class})
    private String password;

    @JsonView({TraineeViews.UpdateReq.class, TraineeViews.GetResp.class, TraineeViews.Status.class})
    private boolean active;

    @JsonView({TraineeViews.Input.class, TraineeViews.UpdateReq.class, TraineeViews.GetResp.class})
    private String address;

    @JsonView({TraineeViews.Input.class, TraineeViews.UpdateReq.class, TraineeViews.GetResp.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonView({TraineeViews.UpdateResp.class, TraineeViews.GetResp.class})
    private List<Training> trainings = new ArrayList<>();
}
