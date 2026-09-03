package com.eventbooking.user.serviceimpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.eventbooking.user.dto.LoginRequest;
import com.eventbooking.user.dto.LoginResponse;
import com.eventbooking.user.entity.User;
import com.eventbooking.user.repository.UserRepository;
import com.eventbooking.user.security.JwtService;
import com.eventbooking.user.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService 
{
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) 
            {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.jwtService = jwtService;
            }

    @Override
    public LoginResponse login(LoginRequest request) 
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found")
                );

        var userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .disabled(!Boolean.TRUE.equals(user.getActive()))
                        .build();

        String token = jwtService.generateToken(
                userDetails,
                user.getId(),
                user.getRole().name()
        );

        return new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}