package com.example.customerservice.service;

import com.example.customerservice.exception.AuthenticationFailedException;
import com.example.customerservice.model.request.LoginRequest;
import com.example.customerservice.model.request.RegisterRequest;
import com.example.customerservice.model.response.LoginResponse;
import com.example.customerservice.model.response.RegisterResponse;
import com.example.customerservice.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;


import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(authenticationManager, jwtUtils, passwordEncoder, jdbcUserDetailsManager);
    }

    @Test
    void registerUserThrowsExceptionForInvalidRequest() {
        RegisterRequest invalidRequest = new RegisterRequest("", "", "");
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.registerUser(invalidRequest);
        });
    }

    @Test
    void registerUserReturnsRegisterResponse() {
        RegisterRequest validRequest = new RegisterRequest("username", "password", "USER");
        RegisterResponse expectedResponse = new RegisterResponse("username", "USER");
        doNothing().when(jdbcUserDetailsManager).createUser(any(UserDetails.class));
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encodedPassword");

        RegisterResponse response = authenticationService.registerUser(validRequest);

        assertEquals(expectedResponse.getUsername(), response.getUsername());
        assertEquals(expectedResponse.getRole(), response.getRole());
    }

    @Test
    void authenticateUserReturnsLoginResponse() {
        LoginRequest validRequest = new LoginRequest("username", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("username");
        when(userDetails.getPassword()).thenReturn("password");
        GrantedAuthority authority = () -> "ROLE_USER";
        doReturn(Collections.singletonList(authority)).when(userDetails).getAuthorities();

        when(jwtUtils.generateTokenFromUsername(userDetails)).thenReturn("jwtToken");

        LoginResponse response = authenticationService.authenticateUser(validRequest);

        assertEquals("username", response.getUsername());
        assertEquals("jwtToken", response.getJwtToken());
    }

    @Test
    void authenticateUserThrowsAuthenticationFailedException() {
        LoginRequest invalidRequest = new LoginRequest("username", "wrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Invalid username or password") {});

        assertThrows(AuthenticationFailedException.class, () -> {
            authenticationService.authenticateUser(invalidRequest);
        });
    }

    @Test
    void registerUserThrowsExceptionForNullRequest() {
        RegisterRequest nullRequest = null;
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.registerUser(nullRequest);
        });
    }

    @Test
    void registerUserThrowsExceptionForMissingUserName() {
        RegisterRequest invalidRequest = new RegisterRequest(null, "hello", "USER");
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.registerUser(invalidRequest);
        });
    }

    @Test
    void registerUserThrowsExceptionForMissingPassword() {
        RegisterRequest invalidRequest = new RegisterRequest("hello", null, "USER");
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.registerUser(invalidRequest);
        });
    }

    @Test
    void registerUserThrowsExceptionForMissingRole() {
        RegisterRequest invalidRequest = new RegisterRequest("hello", "okay", null);
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.registerUser(invalidRequest);
        });
    }

    @Test
    void authenticateUserThrowsExceptionForNull() {
        LoginRequest invalidRequest = null;
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.authenticateUser(invalidRequest);
        });
    }

}