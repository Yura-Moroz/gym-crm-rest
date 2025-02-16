package com.yuramoroz.spring_crm_system.service.security;

import com.yuramoroz.spring_crm_system.dto.auth.JwtAuthenticationResponse;
import com.yuramoroz.spring_crm_system.dto.auth.LoginRequest;
import com.yuramoroz.spring_crm_system.security.JwtTokenProvider;
import com.yuramoroz.spring_crm_system.service.security.impl.BruteForceProtectionService;
import com.yuramoroz.spring_crm_system.service.security.impl.SecurityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private BruteForceProtectionService bruteForceProtectionService;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Clear the security context before each test.
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_success() {
        // Arrange: Create a login request.
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("user")
                .password("password")
                .build();
        String jwtToken = "valid-jwt-token";

        // Simulate that the user is not blocked.
        when(bruteForceProtectionService.isBlocked("user")).thenReturn(false);

        // Create a dummy UserDetails and Authentication result.
        UserDetails userDetails = User.builder()
                .username("user")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(tokenProvider.generateToken(userDetails)).thenReturn(jwtToken);

        // Act
        JwtAuthenticationResponse response = securityService.login(loginRequest);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(jwtToken, response.getToken(), "JWT token should match the expected value");
        verify(bruteForceProtectionService, times(1)).loginSucceeded("user");
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be set in the security context");
    }

    @Test
    void login_blocked() {
        // Arrange: Create a login request.
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("user")
                .password("password")
                .build();
        // Simulate that the user is blocked.
        when(bruteForceProtectionService.isBlocked("user")).thenReturn(true);

        // Act & Assert: Expect a ResponseStatusException with TOO_MANY_REQUESTS status.
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            securityService.login(loginRequest);
        });
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode(), "Expected TOO_MANY_REQUESTS status");
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_invalidCredentials() {
        // Arrange: Create a login request with a wrong password.
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("user")
                .password("wrong-password")
                .build();
        when(bruteForceProtectionService.isBlocked("user")).thenReturn(false);
        // Simulate the authentication manager throwing a BadCredentialsException.
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert: Expect a ResponseStatusException with UNAUTHORIZED status.
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            securityService.login(loginRequest);
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode(), "Expected UNAUTHORIZED status");
        verify(bruteForceProtectionService, times(1)).loginFailed("user");
    }

    @Test
    void logout_success() {
        // Arrange: Provide a valid Authorization header.
        String authHeader = "Bearer valid-token";
        // Act
        String result = securityService.logout(authHeader);
        // Assert
        assertEquals("Logout succeed", result, "Logout should return 'Logout succeed'");
        verify(bruteForceProtectionService, times(1)).blacklistToken("valid-token");
    }

    @Test
    void logout_missingHeader() {
        // Act & Assert: When authHeader is null, expect a ResponseStatusException with BAD_REQUEST status.
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            securityService.logout(null);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "Expected BAD_REQUEST status");
        assertEquals("Missing or invalid Authorization header", exception.getReason(), "Expected specific error message");
        verify(bruteForceProtectionService, never()).blacklistToken(anyString());
    }

    @Test
    void logout_invalidHeader() {
        // Arrange: Provide an invalid Authorization header.
        String authHeader = "InvalidToken dummy-token";
        // Act & Assert: Expect a ResponseStatusException with BAD_REQUEST status.
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            securityService.logout(authHeader);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "Expected BAD_REQUEST status");
        assertEquals("Missing or invalid Authorization header", exception.getReason(), "Expected specific error message");
        verify(bruteForceProtectionService, never()).blacklistToken(anyString());
    }
}