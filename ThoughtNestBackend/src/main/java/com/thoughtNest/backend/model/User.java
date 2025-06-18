package com.thoughtNest.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Entity representing a user in the system.
 * Implements Spring Security's {@link UserDetails} interface for authentication and authorization.
 */
@Entity
@Table(name = "users") // Maps this entity to the "users" table
public class User implements UserDetails {

    /**
     * Primary key for the user entity.
     * Auto-generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique and non-null username used for login and identification.
     */
    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Password for the user account.
     * Must be non-null.
     */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /**
     * User's email address.
     * Must be valid, unique, and non-null.
     */
    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * One-to-One relationship with PasswordResetToken.
     * Allows password recovery via reset token.
     * Cascade all operations and delete token if user is deleted.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private PasswordResetToken passwordResetToken;

    /**
     * One-to-Many relationship with articles authored by the user.
     * Cascade all operations and remove orphan articles when removed from the list.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles;

    // Default constructor required by JPA
    public User() {}

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PasswordResetToken getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(PasswordResetToken passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    // ===== Implementation of UserDetails interface methods =====

    /**
     * Returns the authorities granted to the user.
     * This app currently does not implement roles/permissions.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Indicates whether the user's account has expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     * Could be extended to support email verification, banning, etc.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
