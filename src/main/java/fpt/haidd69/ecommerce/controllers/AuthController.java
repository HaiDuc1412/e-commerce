package fpt.haidd69.ecommerce.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.haidd69.ecommerce.dto.auth.AuthResponse;
import fpt.haidd69.ecommerce.dto.auth.LoginRequest;
import fpt.haidd69.ecommerce.dto.auth.RegisterRequest;
import fpt.haidd69.ecommerce.dto.common.ApiResponse;
import fpt.haidd69.ecommerce.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Authentication controller for user login and registration. CORS is configured
 * centrally in SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Management", description = "User authentication and registration APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @Operation(summary = "User registration", description = "Register new customer account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Registration successful"));
    }
}
