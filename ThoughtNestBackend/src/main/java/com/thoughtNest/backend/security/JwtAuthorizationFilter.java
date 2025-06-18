package com.thoughtNest.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter checks every request for a valid JWT token in the Authorization header.
 * If the token is valid, it sets the corresponding user as authenticated in the Spring Security context.
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor that injects required components.
     *
     * @param jwtUtil utility class for extracting and validating JWT tokens
     * @param userDetailsService service to load user details based on username/email
     */
    public JwtAuthorizationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * This method is triggered once per request. It checks if the request has a valid JWT token.
     * If valid, it sets the authentication in the security context.
     *
     * @param request incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain filter chain to pass the request/response to the next filter
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an input/output error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Read the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the token by removing the "Bearer " prefix
            String token = authHeader.substring(7);

            // Extract the subject (email) from the token
            String email = jwtUtil.extractUsername(token);

            // Ensure token is not null and user is not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details by email
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Validate the token against the user’s email
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    // Create an authentication token with user details and authorities
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Add additional authentication details (like request info)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authenticated user in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Continue with the remaining filter chain
        filterChain.doFilter(request, response);
    }
}
