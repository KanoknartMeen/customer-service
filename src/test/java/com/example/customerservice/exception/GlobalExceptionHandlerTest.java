package com.example.customerservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void handleBadCredentialsExceptionReturnsUnauthorized() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BadCredentialsException ex = new BadCredentialsException("Invalid username or password");

        ResponseEntity<String> response = handler.handleBadCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void handleIllegalArgumentExceptionReturnsBadRequest() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<String> response = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }

    @Test
    void handleAuthenticationExceptionReturnsUnauthorized() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        AuthenticationFailedException ex = new AuthenticationFailedException("Authentication failed", new Exception());

        ResponseEntity<String> response = handler.handleAuthenticationException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication failed", response.getBody());
    }

    @Test
    void handleCustomerNotFoundExceptionReturnsNotFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        CustomerNotFoundException ex = new CustomerNotFoundException("Customer not found");

        ResponseEntity<String> response = handler.handleCustomerNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found", response.getBody());
    }

    @Test
    void handleValidationExceptionsReturnsBadRequest() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                null,
                new BeanPropertyBindingResult(new Object(), "objectName")
        );
        ex.getBindingResult().addError(new FieldError("objectName", "fieldName", "defaultMessage"));

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey("fieldName"));
        assertEquals("defaultMessage", response.getBody().get("fieldName"));
    }

    @Test
    void handleAccessDeniedExceptionReturnsForbidden() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        AccessDeniedException ex = new AccessDeniedException("Access is denied");

        ResponseEntity<String> response = handler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You don't have the permission.", response.getBody());
    }
}