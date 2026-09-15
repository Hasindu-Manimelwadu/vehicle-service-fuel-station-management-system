package com.vehiclestation.auth.controller;

import com.vehiclestation.auth.dto.LoginRequest;
import com.vehiclestation.auth.dto.LoginResponse;
import com.vehiclestation.auth.dto.RegisterRequest;
import com.vehiclestation.auth.dto.RegisterResponse;
import com.vehiclestation.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> currentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("userId", jwt.getClaim("userId"));
        response.put("fullName", jwt.getClaimAsString("name"));
        response.put("email", jwt.getSubject());
        response.put("roles", jwt.getClaimAsStringList("roles"));

        return ResponseEntity.ok(response);
    }
}