package com.thoughtNest.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating account details.
 * Allows changing username and email.
 */
public class UpdateAccountRequest {

    // Optional validations (uncomment if needed)
    // @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    // @Email(message = "Email should be valid")
    private String email;

    // Constructors
    public UpdateAccountRequest() {}

    public UpdateAccountRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
