package com.eventbooking.booking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService 
{
    private final SecretKey signingKey;
    public JwtService(@Value("${jwt.secret}") String secret) 
    {
        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String extractUsername(String token) 
    {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) 
    {
        Number userId = extractAllClaims(token)
                .get("userId", Number.class);
        return userId != null ? userId.longValue() : null;
    }

    public String extractRole(String token) 
    {
        return extractAllClaims(token)
                .get("role", String.class);
    }

    public boolean isTokenValid(String token) 
    {
        try 
        {
            Claims claims = extractAllClaims(token);
            return claims.getSubject() != null
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());

        }
        catch (Exception ex) 
        {
            return false;
        }
    }

    private Claims extractAllClaims(String token) 
    {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}