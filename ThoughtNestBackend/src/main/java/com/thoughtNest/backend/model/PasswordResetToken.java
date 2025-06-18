package com.thoughtNest.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a password reset token associated with a user.
 * This is used during the "forgot password" workflow to validate token-based password resets.
 */
@Entity
public class PasswordResetToken {

    /**
     * Primary key for the password reset token.
     * Automatically generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The actual token string sent to the user via email.
     */
    private String token;

    /**
     * The user associated with this token.
     * This is a one-to-one relationship where the foreign key column is non-nullable.
     */
    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    @JsonBackReference
    private User user;

    /**
     * The date and time when the token expires.
     * After this timestamp, the token is no longer valid.
     */
    private LocalDateTime expiryDate;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
