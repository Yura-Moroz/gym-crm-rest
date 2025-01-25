package com.yuramoroz.spring_crm_system.dto;

import com.yuramoroz.spring_crm_system.entity.Training;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerDto extends UserDto{

    private String specialization;

    private List<Training> trainingList;
}
