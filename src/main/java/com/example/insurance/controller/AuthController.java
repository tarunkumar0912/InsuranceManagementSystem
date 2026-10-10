package com.example.insurance.controller;

import com.example.insurance.dto.request.OtpRequest;
import com.example.insurance.dto.request.OtpVerificationRequest;
import com.example.insurance.dto.request.UserRegistrationRequest;
import com.example.insurance.dto.response.AuthResponse;
import com.example.insurance.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody UserRegistrationRequest request) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message",
                        "Account registered successfully. Request an OTP to log in."
                ));
    }

    @PostMapping("/request-otp")
    public ResponseEntity<Map<String, String>> requestOtp(
            @Valid @RequestBody OtpRequest request) {

        authService.requestOtp(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "If the account is eligible, an OTP will be issued."
                )
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {

        AuthResponse response = authService.verifyOtp(request);

        return ResponseEntity.ok(response);
    }
}