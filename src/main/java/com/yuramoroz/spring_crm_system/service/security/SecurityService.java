package com.yuramoroz.spring_crm_system.service.security;

import com.yuramoroz.spring_crm_system.dto.auth.JwtAuthenticationResponse;
import com.yuramoroz.spring_crm_system.dto.auth.LoginRequest;

public interface SecurityService {

    public JwtAuthenticationResponse login(LoginRequest loginRequest);

    public String logout(String authHeader);

}
