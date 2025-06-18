package com.thoughtNest.backend.security;

import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of {@link UserDetailsService} for Spring Security.
 *
 * This service is responsible for loading user-specific data during authentication.
 * It retrieves a {@link User} from the database based on the login identifier (email),
 * and wraps it in a {@link UserPrincipal} which implements {@link UserDetails}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor-based dependency injection of UserRepository.
     *
     * @param userRepository repository for accessing user data
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address (used as login identifier).
     *
     * @param identifier the email provided during login
     * @return the UserPrincipal for authentication
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        System.out.println("Received login identifier: " + identifier); // For debugging

        // Lookup user by email
        User user = userRepository.findByEmail(identifier)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + identifier));

        // Wrap the User in a UserPrincipal object
        return new UserPrincipal(user);
    }
}
