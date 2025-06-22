package com.thoughtNest.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thoughtNest.backend.model.PasswordResetToken;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.repository.PasswordResetTokenRepository;
import com.thoughtNest.backend.repository.UserRepository;

/**
 * Service class for handling all user-related operations:
 * registration, password resets, and updates.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    /**
     * Check if a username is already registered.
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if an email is already registered.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Save a new user into the database with an encoded password.
     */
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Find a user by their username.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find a user by their email address.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Creates a new password reset token for a user.
     * Removes any existing token first to prevent reuse.
     */
    @Transactional
    public String createPasswordResetToken(User user) {
        // Remove existing token if it exists for the user
        passwordResetTokenRepository.deleteByUser(user.getId());
        passwordResetTokenRepository.flush(); // Ensure deletion is committed

        // Create a new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // Token valid for 30 minutes

        passwordResetTokenRepository.save(resetToken);
        return token;
    }

    /**
     * Validates if a given reset token exists and is still valid (not expired).
     */
    public boolean validatePasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    /**
     * Resets the user's password if the token is valid and not expired.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = optionalToken.get();

        // Check token expiry
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Update the user's password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate token after use
        passwordResetTokenRepository.delete(resetToken);
        return true;
    }

    /**
     * Updates the username and/or email of a user.
     * Checks for uniqueness before applying changes.
     */
    @Transactional
    public User updateUsernameOrEmailOrBoth(String currentEmail, String newUsername, String newEmail) {
        // Fetch user using their current email
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update username if provided and different
        if (newUsername != null && !newUsername.isBlank() && !user.getUsername().equals(newUsername)) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(newUsername);
        }

        // Update email if provided and different
        if (newEmail != null && !newEmail.isBlank() && !user.getEmail().equals(newEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(newEmail);
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmailWithArticles(String email) {
    return userRepository.findByEmailWithArticles(email);
}

}
