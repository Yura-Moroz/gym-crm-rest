package com.yuramoroz.spring_crm_system.controller;

import com.yuramoroz.spring_crm_system.dto.auth.JwtAuthenticationResponse;
import com.yuramoroz.spring_crm_system.dto.auth.LoginRequest;
import com.yuramoroz.spring_crm_system.service.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final SecurityService securityService;

    @Operation(summary = "To log in user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(securityService.login(loginRequest));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't authorize user." +
                    "\nPlease check all credentials and try again");
        }
    }

    @Operation(summary = "To log out user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(securityService.logout(authHeader));
    }
}
