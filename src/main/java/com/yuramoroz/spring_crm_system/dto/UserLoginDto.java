package com.yuramoroz.spring_crm_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Old password is mandatory")
    private String oldPassword;

    @NotBlank(message = "New password can't be empty")
    private String newPassword;
}
