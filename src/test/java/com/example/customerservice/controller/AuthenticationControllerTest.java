package com.example.customerservice.controller;

import com.example.customerservice.model.request.LoginRequest;
import com.example.customerservice.model.request.RegisterRequest;
import com.example.customerservice.model.response.LoginResponse;
import com.example.customerservice.model.response.RegisterResponse;
import com.example.customerservice.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUserCreatesUserSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "USER");
        RegisterResponse registerResponse = new RegisterResponse("username", "USER");
        when(authenticationService.registerUser(registerRequest)).thenReturn(registerResponse);

        ResponseEntity<RegisterResponse> response = authenticationController.registerUser(registerRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(registerResponse, response.getBody());
    }

    @Test
    void authenticateUserReturnsTokenSuccessfully() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResponse loginResponse = new LoginResponse("username", List.of("USER"), "token");
        when(authenticationService.authenticateUser(loginRequest)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = authenticationController.authenticateUser(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(loginResponse, response.getBody());
    }

}