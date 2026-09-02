package com.eventbooking.user.exception;

public class ResourceAlreadyExistsException extends RuntimeException 
{
    public ResourceAlreadyExistsException(String message) 
    {
        super(message);
    }
}