package com.thoughtNest.backend.controller;

import com.thoughtNest.backend.dto.AuthResponse;
import com.thoughtNest.backend.dto.LoginRequest;
import com.thoughtNest.backend.dto.UpdateAccountRequest;
import com.thoughtNest.backend.dto.UserAccountInfoDto;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.util.ResponseHandler;
import com.thoughtNest.backend.security.JwtUtil;
import com.thoughtNest.backend.service.EmailService;
import com.thoughtNest.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

/**
 * AuthController handles all authentication- and account-related operations,
 * including user signup, login, password reset, and account information.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private EmailService emailService;

    /**
     * Endpoint to register a new user.
     * Rejects registration if the username already exists.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseHandler.generateResponse("Username already exists", HttpStatus.BAD_REQUEST, null);
        }
        return ResponseHandler.generateResponse("User created successfully", HttpStatus.OK, userService.saveUser(user));
    }

    /**
     * Endpoint to log in a user.
     * Authenticates using email and password, and returns a JWT on success.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Optional<User> userOpt = userService.findByEmail(request.getIdentifier());
            if (userOpt.isEmpty()) {
                return ResponseHandler.generateResponse("Invalid email or password", HttpStatus.UNAUTHORIZED, null);
            }

            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getEmail());

            return ResponseHandler.generateResponse(
                    "Login successful", HttpStatus.OK, new AuthResponse(token, user.getUsername())
            );
        } catch (BadCredentialsException e) {
            return ResponseHandler.generateResponse("Invalid email or password", HttpStatus.UNAUTHORIZED, null);
        }
    }

    /**
     * Endpoint to trigger password reset via email.
     * Sends a reset token to the user's email if found.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseHandler.generateResponse("User not found", HttpStatus.BAD_REQUEST, null);
        }

        String token = userService.createPasswordResetToken(userOpt.get());

        try {
            emailService.sendPasswordResetEmail(email, token);
            return ResponseHandler.generateResponse("Password reset email sent", HttpStatus.OK, null);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error sending email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    /**
     * Endpoint to reset the password using a token sent via email.
     * Returns error if the token is invalid or expired.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");
        boolean result = userService.resetPassword(token, newPassword);

        if (!result) {
            return ResponseHandler.generateResponse("Invalid or expired token", HttpStatus.BAD_REQUEST, null);
        }

        return ResponseHandler.generateResponse("Password successfully reset", HttpStatus.OK, null);
    }

    /**
     * Endpoint to update account information (email and/or username).
     * Requires authentication; uses Principal to identify the current user.
     */
    @PatchMapping("/account")
    public ResponseEntity<?> updateAccount(@RequestBody UpdateAccountRequest request, Principal principal) {
        String currentEmail = principal.getName();
        try {
            User updatedUser = userService.updateUsernameOrEmailOrBoth(currentEmail, request.getUsername(), request.getEmail());

            boolean emailChanged = request.getEmail() != null && !request.getEmail().equals(currentEmail);
            String message = "Account updated successfully";

            if (emailChanged) {
                message += ". Please log in again to continue.";
            }

            return ResponseHandler.generateResponse(message, HttpStatus.OK, updatedUser);
        } catch (RuntimeException e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    /**
     * Endpoint to fetch the current user's account info.
     * Includes counts for total articles and published articles.
     */
    @GetMapping("/account")
    public ResponseEntity<?> getCurrentAccount(Principal principal) {
        String username = principal.getName();
        System.out.println("Current Username fetched from UserPrincipal in get account:" + username);

        Optional<User> userOpt = userService.findByEmail(username);

        if (userOpt.isEmpty()) {
            return ResponseHandler.generateResponse("User not found", HttpStatus.NOT_FOUND, null);
        }

        User user = userOpt.get();
        int articleCount = user.getArticles() != null ? user.getArticles().size() : 0;
        int publishedCount = (int) user.getArticles().stream().filter(a -> Boolean.TRUE.equals(a.getPublished())).count();

        UserAccountInfoDto dto = new UserAccountInfoDto(user.getUsername(), user.getEmail(), articleCount, publishedCount);

        return ResponseHandler.generateResponse("User fetched successfully", HttpStatus.OK, dto);
    }
}
