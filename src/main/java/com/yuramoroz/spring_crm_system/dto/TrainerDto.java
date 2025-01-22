package com.yuramoroz.spring_crm_system.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerDto extends UserDto{

    private String specialization;

}
