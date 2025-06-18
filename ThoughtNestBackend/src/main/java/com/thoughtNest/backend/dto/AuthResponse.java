package com.thoughtNest.backend.dto;

/**
 * DTO returned to the client upon successful authentication.
 * Contains the JWT token and the authenticated user's username.
 */
public class AuthResponse {
    private String token;
    private String username;

    /**
     * Constructs an AuthResponse with the provided token and username.
     *
     * @param token    The JWT token generated after login.
     * @param username The username of the authenticated user.
     */
    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    // Getters and Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
