package com.thoughtNest.backend;

import com.thoughtNest.backend.model.PasswordResetToken;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.repository.PasswordResetTokenRepository;
import com.thoughtNest.backend.repository.UserRepository;
import com.thoughtNest.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Uses Mockito to isolate dependencies and verify business logic for:
 * - User registration
 * - Existence checks
 * - Password encoding
 * - Password reset token management
 * - Password reset functionality
 */
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private PasswordResetToken resetToken;

    /**
     * Initializes mocks and test objects before each test runs.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPassword("plainPassword");

        resetToken = new PasswordResetToken();
        resetToken.setToken("token123");
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
    }

    /**
     * Tests that user existence by username is correctly identified.
     */
    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername("user1")).thenReturn(true);
        assertTrue(userService.existsByUsername("user1"));

        when(userRepository.existsByUsername("unknown")).thenReturn(false);
        assertFalse(userService.existsByUsername("unknown"));
    }

    /**
     * Tests that user existence by email is correctly identified.
     */
    @Test
    void testExistsByEmail() {
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("user1@example.com"));

        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);
        assertFalse(userService.existsByEmail("unknown@example.com"));
    }

    /**
     * Tests that a user is saved with an encoded password.
     */
    @Test
    void testSaveUser() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.saveUser(user);
        assertEquals("encodedPassword", saved.getPassword());
        verify(userRepository).save(user);
    }

    /**
     * Tests fetching a user by username.
     */
    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        Optional<User> found = userService.findByUsername("user1");
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    /**
     * Tests fetching a user by email.
     */
    @Test
    void testFindByEmail() {
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));
        Optional<User> found = userService.findByEmail("user1@example.com");
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    /**
     * Tests that an old token is deleted and a new password reset token is created.
     */
    @Test
    void testCreatePasswordResetToken() {
        doNothing().when(passwordResetTokenRepository).deleteByUser(user.getId());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = userService.createPasswordResetToken(user);
        assertNotNull(token);
        verify(passwordResetTokenRepository).deleteByUser(user.getId());
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    /**
     * Tests that a valid token is accepted during validation.
     */
    @Test
    void testValidatePasswordResetToken_Valid() {
        when(passwordResetTokenRepository.findByToken("token123")).thenReturn(Optional.of(resetToken));
        assertTrue(userService.validatePasswordResetToken("token123"));
    }

    /**
     * Tests that an expired token is rejected during validation.
     */
    @Test
    void testValidatePasswordResetToken_Expired() {
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(passwordResetTokenRepository.findByToken("token123")).thenReturn(Optional.of(resetToken));
        assertFalse(userService.validatePasswordResetToken("token123"));
    }

    /**
     * Tests that a non-existent token is rejected during validation.
     */
    @Test
    void testValidatePasswordResetToken_NotFound() {
        when(passwordResetTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        assertFalse(userService.validatePasswordResetToken("invalid"));
    }

    /**
     * Tests that password reset works correctly when the token is valid.
     * Also checks that the user password is updated and the token is deleted.
     */
    @Test
    void testResetPassword_Success() {
        when(passwordResetTokenRepository.findByToken("token123")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        boolean result = userService.resetPassword("token123", "newPassword");

        assertTrue(result);
        assertEquals("encodedNewPassword", resetToken.getUser().getPassword());
        verify(userRepository).save(resetToken.getUser());
        verify(passwordResetTokenRepository).delete(resetToken);
    }

    /**
     * Tests that reset fails if the token is expired.
     */
    @Test
    void testResetPassword_Expired() {
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(passwordResetTokenRepository.findByToken("token123")).thenReturn(Optional.of(resetToken));

        boolean result = userService.resetPassword("token123", "newPassword");

        assertFalse(result);
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, never()).delete(any());
    }

    /**
     * Tests that reset fails if the token does not exist.
     */
    @Test
    void testResetPassword_TokenNotFound() {
        when(passwordResetTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        boolean result = userService.resetPassword("invalid", "newPassword");

        assertFalse(result);
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, never()).delete(any());
    }
}
