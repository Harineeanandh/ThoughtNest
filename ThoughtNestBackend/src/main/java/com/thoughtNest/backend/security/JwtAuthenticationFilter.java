package com.thoughtNest.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtNest.backend.dto.LoginRequest;
import com.thoughtNest.backend.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This filter handles user authentication by intercepting login requests.
 * If authentication is successful, it generates a JWT token and returns it in the response.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    /**
     * Constructor that initializes the filter with a custom AuthenticationManager and JWT utility class.
     *
     * @param authenticationManager the Spring Security authentication manager
     * @param jwtUtil utility class for generating JWT tokens
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setAuthenticationManager(authenticationManager); // Required to use the custom auth manager
    }

    /**
     * This method is triggered when a login request is received.
     * It reads the login request (JSON) from the input stream and attempts to authenticate the user.
     *
     * @param request the incoming HTTP request containing user credentials
     * @param response the HTTP response
     * @return Authentication result (success or failure)
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            // Parse JSON body into LoginRequest object (containing identifier and password)
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            // Create authentication token using identifier (email or username) and password
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword());

            // Authenticate using Spring Security’s authentication manager
            return getAuthenticationManager().authenticate(authToken);

        } catch (IOException e) {
            // If JSON parsing fails, wrap it in a runtime exception
            throw new RuntimeException("Failed to parse login request", e);
        }
    }

    /**
     * Called automatically when authentication is successful.
     * Generates a JWT token and returns it in the HTTP response as JSON.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param chain the filter chain
     * @param authResult the result of the successful authentication
     * @throws IOException if writing the response fails
     * @throws ServletException if filter chain fails
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        // Retrieve authenticated user's details
        UserPrincipal userPrincipal = (UserPrincipal) authResult.getPrincipal();
        User user = userPrincipal.getUser();

        // Generate JWT token using user's email
        String token = jwtUtil.generateToken(user.getEmail());

        // Prepare JSON response containing the token
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        // Write the token as JSON to the response body
        new ObjectMapper().writeValue(response.getWriter(), tokenMap);
    }

    /**
     * Called automatically when authentication fails.
     * Returns a JSON response with the failure reason.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param failed the exception containing the failure reason
     * @throws IOException if writing the response fails
     * @throws ServletException if filter chain fails
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {

        // Set HTTP status code to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Prepare error response in JSON format
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Authentication failed: " + failed.getMessage());

        // Write the error to the response body
        new ObjectMapper().writeValue(response.getWriter(), errorMap);
    }
}
