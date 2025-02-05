package com.example.customerservice.configuration;

import com.example.customerservice.security.AuthTokenFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig();
        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("user", "password"));

    }

    @Test
    void authenticationJwtTokenFilterReturnsAuthTokenFilter() {
        AuthTokenFilter filter = securityConfig.authenticationJwtTokenFilter();
        assertNotNull(filter, "AuthTokenFilter should not be null");
    }

    @Test
    void passwordEncoderReturnsBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder, "PasswordEncoder should not be null");
        assertTrue(encoder instanceof BCryptPasswordEncoder, "PasswordEncoder should be an instance of BCryptPasswordEncoder");
    }

    @Test
    void jwtSecretReturnsBase64EncodedKey() {
        String secret = securityConfig.jwtSecret();
        assertNotNull(secret, "JWT Secret should not be null");
        assertEquals(44, secret.length(), "JWT Secret should be a Base64-encoded 32-byte key");
    }

    @Test
    void testAuthenticationJwtTokenFilter() {
        AuthTokenFilter filter = securityConfig.authenticationJwtTokenFilter();
        assertNotNull(filter, "AuthTokenFilter should not be null");
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder, "PasswordEncoder should not be null");
        String rawPassword = "test123";
        String encodedPassword = encoder.encode(rawPassword);
        assertNotEquals(rawPassword, encodedPassword, "Encoded password should be different from raw password");
        assertTrue(encoder.matches(rawPassword, encodedPassword), "PasswordEncoder should correctly match passwords");
    }

    @Test
    void testJwtSecret() {
        String secret = securityConfig.jwtSecret();
        assertNotNull(secret, "JWT Secret should not be null");
        assertEquals(44, secret.length(), "JWT Secret should be a Base64-encoded 32-byte key");
    }

    @Test
    void shouldReturnAuthenticationManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(authenticationManager);

        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }
}
