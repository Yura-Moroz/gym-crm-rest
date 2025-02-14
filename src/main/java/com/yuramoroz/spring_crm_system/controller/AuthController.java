package com.yuramoroz.spring_crm_system.controller;

import com.yuramoroz.spring_crm_system.dto.auth.JwtAuthenticationResponse;
import com.yuramoroz.spring_crm_system.dto.auth.LoginRequest;
import com.yuramoroz.spring_crm_system.security.BruteForceProtectionService;
import com.yuramoroz.spring_crm_system.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final BruteForceProtectionService bruteForceProtectionService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUserName();
        if (bruteForceProtectionService.isBlocked(username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("User is temporarily blocked due to multiple failed login attempts. Please, try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
            // On successful authentication, reset failed attempts
            bruteForceProtectionService.loginSucceeded(username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = tokenProvider.generateToken(userDetails);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (BadCredentialsException ex) {
            // Record failed attempt
            bruteForceProtectionService.loginFailed(username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {

        return ResponseEntity.ok("Logout successful");
    }
}
