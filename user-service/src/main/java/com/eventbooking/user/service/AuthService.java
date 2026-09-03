package com.eventbooking.user.service;

import com.eventbooking.user.dto.LoginRequest;
import com.eventbooking.user.dto.LoginResponse;

public interface AuthService 
{
    LoginResponse login(LoginRequest request);
}