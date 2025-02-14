package com.yuramoroz.spring_crm_system.service.security.impl;

import com.yuramoroz.spring_crm_system.dto.auth.JwtAuthenticationResponse;
import com.yuramoroz.spring_crm_system.dto.auth.LoginRequest;
import com.yuramoroz.spring_crm_system.security.JwtTokenProvider;
import com.yuramoroz.spring_crm_system.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final BruteForceProtectionService bruteForceProtectionService;

    @Override
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUserName();

        if (bruteForceProtectionService.isBlocked(username)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "User is temporarily blocked due to multiple failed login attempts. Please, try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
            /** On successful authentication, reset failed attempts */
            bruteForceProtectionService.loginSucceeded(username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = tokenProvider.generateToken(userDetails);
            return new JwtAuthenticationResponse(jwt);

        } catch (BadCredentialsException ex) {
            /** Record failed attempt */
            bruteForceProtectionService.loginFailed(username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    @Override
    public String logout(String authHeader) {
        return Optional.ofNullable(authHeader)
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring("Bearer ".length()).trim())
                .map(token -> {
                    bruteForceProtectionService.blacklistToken(token);
                    return "Logout succeed";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header"));
    }
}
