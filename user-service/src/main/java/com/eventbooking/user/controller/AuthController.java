package com.eventbooking.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.user.dto.LoginRequest;
import com.eventbooking.user.dto.LoginResponse;
import com.eventbooking.user.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController 
{
    private final AuthService authService;
    public AuthController(AuthService authService) 
    {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) 
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }
}