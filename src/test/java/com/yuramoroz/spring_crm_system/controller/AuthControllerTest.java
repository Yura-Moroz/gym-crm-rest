package com.yuramoroz.spring_crm_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuramoroz.spring_crm_system.dto.auth.JwtAuthenticationResponse;
import com.yuramoroz.spring_crm_system.dto.auth.LoginRequest;
import com.yuramoroz.spring_crm_system.security.JwtTokenProvider;
import com.yuramoroz.spring_crm_system.service.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @Test
    void testLoginSuccess() throws Exception {
        //Given
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("user")
                .password("password")
                .build();
        JwtAuthenticationResponse jwtResponse = JwtAuthenticationResponse.builder()
                .token("dummy-token")
                .build();

        //When-Then
        when(securityService.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("dummy-token"));

        verify(securityService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testLoginFails() throws Exception {
        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("user")
                .password("wrong-password")
                .build();

        // When-Then
        when(securityService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Can't authorize user.\nPlease check all credentials and try again"));

        verify(securityService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testLogoutSuccess() throws Exception {
        when(securityService.logout(anyString())).thenReturn("Logout succeed");

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", anyString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout succeed"));

        verify(securityService, times(1)).logout(anyString());
    }

}