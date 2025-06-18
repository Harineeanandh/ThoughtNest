package com.thoughtNest.backend.dto;

/**
 * DTO representing the response returned upon successful JWT authentication.
 * Includes the JWT token and the authenticated user's username.
 */
public class JwtResponse {

    private final String token;
    private final String username;

    /**
     * Constructs a new JwtResponse with the given token and username.
     *
     * @param token    the JWT token string
     * @param username the username of the authenticated user
     */
    public JwtResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    /**
     * @return the JWT token string
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the username of the authenticated user
     */
    public String getUsername() {
        return username;
    }
}
