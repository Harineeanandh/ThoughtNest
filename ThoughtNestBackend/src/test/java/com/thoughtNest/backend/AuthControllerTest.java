package com.thoughtNest.backend;

import com.thoughtNest.backend.controller.AuthController;
import com.thoughtNest.backend.dto.AuthResponse;
import com.thoughtNest.backend.dto.LoginRequest;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.security.JwtUtil;
import com.thoughtNest.backend.service.EmailService;
import com.thoughtNest.backend.service.UserService;
import com.thoughtNest.backend.util.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for the AuthController using Spring's MockMvc and Mockito.
 *
 * This class simulates HTTP POST requests to the authentication-related endpoints
 * (/signup, /login, /forgot-password, /reset-password) and verifies the correctness
 * of the controller logic, without starting a full server.
 */
class AuthControllerTest {

    // Used to simulate HTTP requests to controller endpoints
    private MockMvc mockMvc;

    // Inject the mocked dependencies into this controller
    @InjectMocks
    private AuthController authController;

    // Mocked dependencies
    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    // Used to serialize request payloads into JSON
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initialize the mocks and set up the controller before each test.
     * This avoids full application context loading.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    /**
     * Test successful user signup.
     * Verifies a user is saved and a success message is returned.
     */
    @Test
    void signup_Success() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    /**
     * Test signup failure due to existing username.
     */
    @Test
    void signup_UsernameExists() throws Exception {
        User user = new User();
        user.setUsername("existinguser");

        when(userService.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    /**
     * Test successful login with valid credentials.
     * Mocks Spring Security authentication and JWT token generation.
     */
    @Test
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("test@example.com");
        loginRequest.setPassword("password");

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test@example.com")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    /**
     * Test login failure due to incorrect credentials.
     */
    @Test
    void login_BadCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    /**
     * Test forgot-password when user is not found.
     */
    @Test
    void forgotPassword_UserNotFound() throws Exception {
        Map<String, String> payload = Map.of("email", "notfound@example.com");

        when(userService.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    /**
     * ✅ Test forgot-password flow when everything works fine.
     * Ensures a reset token is generated and email is sent.
     */
    @Test
    void forgotPassword_Success() throws Exception {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Map<String, String> payload = Map.of("email", email);

        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(userService.createPasswordResetToken(user)).thenReturn("reset-token");

        doNothing().when(emailService).sendPasswordResetEmail(email, "reset-token");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset email sent"));
    }

    /**
     * Test forgot-password when email fails to send.
     * Mocks an internal server error from the email service.
     */
    @Test
    void forgotPassword_EmailSendFailure() throws Exception {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Map<String, String> payload = Map.of("email", email);

        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(userService.createPasswordResetToken(user)).thenReturn("reset-token");

        doThrow(new RuntimeException("SMTP server down"))
                .when(emailService).sendPasswordResetEmail(email, "reset-token");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Error sending email")));
    }

    /**
     * Test reset-password with valid token and new password.
     */
    @Test
    void resetPassword_Success() throws Exception {
        Map<String, String> payload = Map.of(
                "token", "valid-token",
                "newPassword", "newpass123"
        );

        when(userService.resetPassword("valid-token", "newpass123")).thenReturn(true);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password successfully reset"));
    }

    /**
     * Test reset-password with invalid or expired token.
     */
    @Test
    void resetPassword_InvalidOrExpiredToken() throws Exception {
        Map<String, String> payload = Map.of(
                "token", "invalid-token",
                "newPassword", "newpass123"
        );

        when(userService.resetPassword("invalid-token", "newpass123")).thenReturn(false);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }
}
