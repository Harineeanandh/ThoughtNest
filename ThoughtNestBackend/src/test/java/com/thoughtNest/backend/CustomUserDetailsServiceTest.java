package com.thoughtNest.backend;

import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.repository.UserRepository;
import com.thoughtNest.backend.security.CustomUserDetailsService;
import com.thoughtNest.backend.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for CustomUserDetailsService.
 *
 * This class verifies that user details are correctly loaded using email
 * and proper exceptions are thrown when a user is not found.
 */
class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    /**
     * Sets up mock UserRepository and initializes the service before each test.
     */
    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    /**
     * Test to ensure loadUserByUsername() works when user is found by email.
     *
     * Verifies:
     * - The returned UserDetails has correct email and password.
     * - The result is an instance of UserPrincipal.
     */
    @Test
    void loadUserByUsername_successfulByEmail() {
        User mockUser = new User();
        mockUser.setUsername("john");
        mockUser.setPassword("secret");
        mockUser.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        var userDetails = userDetailsService.loadUserByUsername("john@example.com");

        assertEquals("john@example.com", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertTrue(userDetails instanceof UserPrincipal);
    }

    /**
     * Test to ensure loadUserByUsername() throws UsernameNotFoundException
     * when the user is not found by email.
     */
    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("notfound@example.com"));
    }
}
