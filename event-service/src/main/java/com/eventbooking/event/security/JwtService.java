package com.eventbooking.event.security;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService 
{
    private final String secret;
    public JwtService(
            @Value("${jwt.secret}") String secret) 
    {
        this.secret = secret;
    }

    public String extractUsername(String token) 
    {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) 
    {
        return extractClaim(
                token,
                claims -> claims.get("userId", Long.class)
        );
    }

    public String extractRole(String token) 
    {
        return extractClaim(
                token,
                claims -> claims.get("role", String.class)
        );
    }

    public boolean isTokenValid(String token) 
    {
        try 
        {
            extractAllClaims(token);
            return true;
        } catch (Exception exception) 
        {
            return false;
        }
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) 
    {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) 
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() 
    {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}