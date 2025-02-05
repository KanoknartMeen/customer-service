package com.example.customerservice.service;

import com.example.customerservice.exception.AuthenticationFailedException;
import com.example.customerservice.model.request.LoginRequest;
import com.example.customerservice.model.request.RegisterRequest;
import com.example.customerservice.model.response.LoginResponse;
import com.example.customerservice.model.response.RegisterResponse;
import com.example.customerservice.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final JdbcUserDetailsManager jdbcUserDetailsManager;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, JdbcUserDetailsManager jdbcUserDetailsManager) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.jdbcUserDetailsManager = jdbcUserDetailsManager;
    }

    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        if (!this.isValidRequest(registerRequest)) {
             throw new IllegalArgumentException("Missing fields");
        }
        UserDetails user = User.withUsername(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(registerRequest.getRole())
                .build();
        jdbcUserDetailsManager.createUser(user);
        return new RegisterResponse(user.getUsername(), registerRequest.getRole());
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("LoginRequest cannot be null");
        }
        try {
            Authentication authentication = authenticate(loginRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
            List<String> roles = getRoles(userDetails);

            return new LoginResponse(userDetails.getUsername(), roles, jwtToken);
        } catch (AuthenticationException exception) {
            throw new AuthenticationFailedException("Invalid username or password", exception);
        }
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    private List<String> getRoles(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
    }

    boolean isValidRequest(RegisterRequest registerRequest) {
        return registerRequest != null && registerRequest.getUsername() != null && registerRequest.getPassword() != null && registerRequest.getRole() != null;
    }
}