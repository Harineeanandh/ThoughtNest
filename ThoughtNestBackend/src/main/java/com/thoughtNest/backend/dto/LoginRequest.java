package com.thoughtNest.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a login request.
 * The user can log in using either a username or email, along with a password.
 */
public class LoginRequest {

    @NotBlank(message = "Username or Email is required")
    private String identifier; // Can be username or email

    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default constructor.
     */
    public LoginRequest() {}

    /**
     * Constructs a new LoginRequest with the specified identifier and password.
     *
     * @param identifier the username or email
     * @param password   the password
     */
    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Getters and Setters

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
