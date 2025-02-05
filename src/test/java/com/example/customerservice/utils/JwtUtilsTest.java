package com.example.customerservice.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetails userDetails;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        jwtUtils = new JwtUtils("ewyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9sdfwecvwesrew", 6000000);
    }

    @Test
    void getJwtFromHeaderReturnsToken() {
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");
        String token = jwtUtils.getJwtFromHeader(request);
        assertEquals("testToken", token);
    }

    @Test
    void getJwtFromHeaderReturnsNullWhenNoBearer() {
        when(request.getHeader("Authorization")).thenReturn("testToken");
        String token = jwtUtils.getJwtFromHeader(request);
        assertNull(token);
    }

    @Test
    void getJwtFromHeaderReturnsNullWhenEmptyToken() {
        when(request.getHeader("Authorization")).thenReturn("");
        String token = jwtUtils.getJwtFromHeader(request);
        assertNull(token);
    }

    @Test
    void getJwtFromHeaderReturnsNullWhenNullToken() {
        when(request.getHeader("Authorization")).thenReturn(null);
        String token = jwtUtils.getJwtFromHeader(request);
        assertNull(token);
    }

    @Test
    void generateTokenFromUsernameReturnsToken() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtUtils.generateTokenFromUsername(userDetails);
        assertNotNull(token);
    }

    @Test
    void getUserNameFromJwtTokenReturnsUsername() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtUtils.generateTokenFromUsername(userDetails);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("testUser", username);
    }

    @Test
    void isValidReturnsTrueForValidToken() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtUtils.generateTokenFromUsername(userDetails);
        boolean isValid = jwtUtils.isValid(token);
        assertTrue(isValid);
    }

    @Test
    void isValidReturnsFalseForInvalidToken() {
        boolean isValid = jwtUtils.isValid("invalidToken");
        assertFalse(isValid);
    }

    @Test
    void isValidReturnsFalseForEmptyToken() {
        boolean isValid = jwtUtils.isValid("");
        assertFalse(isValid);
    }

    @Test
    void isValidReturnsFalseForExpiredToken() {
        JwtUtils jwtUtils = new JwtUtils("ewyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9sdfwecvwesrew", -1);
        when(userDetails.getUsername()).thenReturn("testUser");
        String expiredToken = jwtUtils.generateTokenFromUsername(userDetails);
        boolean isValid = jwtUtils.isValid(expiredToken);
        assertFalse(isValid);
    }

}