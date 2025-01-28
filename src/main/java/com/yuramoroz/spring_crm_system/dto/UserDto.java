package com.yuramoroz.spring_crm_system.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public abstract class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String userName;

    private String password;

    private boolean active;

}
