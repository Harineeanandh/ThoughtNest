package com.thoughtNest.backend.security;

import com.thoughtNest.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * A custom implementation of Spring Security's UserDetails interface.
 * This class wraps the application's User entity to adapt it for authentication.
 */
public class UserPrincipal implements UserDetails {

    // The actual User entity from the database
    private final User user;

    // Constructor to initialize the UserPrincipal with a User
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Returns the underlying User object.
     * Useful when you want to access more user data in controllers/services.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the authorities (roles/permissions) granted to the user.
     * For now, no roles are used, so an empty list is returned.
     * You can modify this to return roles in the future.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Returns the user's password, used internally by Spring Security during authentication.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the identifier used for login — in this case, the user's email.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Whether the user's account has expired.
     * Always returns true since account expiration isn't being tracked.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Whether the user is locked or unlocked.
     * Always returns true since account locking isn't implemented.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Whether the user's credentials (password) have expired.
     * Always returns true as password expiration isn't enforced.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Whether the user is enabled or disabled.
     * Always returns true. You can change this to support user activation.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Helper method to return the user’s display name (username).
     * Used when you want to show the name instead of email.
     */
    public String getDisplayUsername() {
        return user.getUsername();
    }
}
