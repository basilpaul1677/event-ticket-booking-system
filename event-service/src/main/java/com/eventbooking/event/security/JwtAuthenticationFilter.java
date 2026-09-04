package com.eventbooking.event.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter 
{
    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService) 
    {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException 
        {
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) 
        {
            jwt = authHeader.substring(7);
        }
        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) 
        {

            try 
            {
                if (jwtService.isTokenValid(jwt)) 
                {
                    String username = jwtService.extractUsername(jwt);
                    String role = jwtService.extractRole(jwt);
                    Long userId = jwtService.extractUserId(jwt);

                    JwtUserPrincipal principal =
                            new JwtUserPrincipal(
                                    userId,
                                    username,
                                    role
                            );

                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role
                            );

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    List.of(authority)
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );
                }
            }
            catch (Exception exception) 
            {
                SecurityContextHolder
                        .clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}