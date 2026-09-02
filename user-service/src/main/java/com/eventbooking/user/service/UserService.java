package com.eventbooking.user.service;

import com.eventbooking.user.dto.RegisterUserRequest;
import com.eventbooking.user.dto.UserResponse;

public interface UserService 
{
    UserResponse registerUser(RegisterUserRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
}