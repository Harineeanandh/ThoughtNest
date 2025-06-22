package com.thoughtNest.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thoughtNest.backend.model.User;

/**
 * Repository interface for {@link User} entities.
 *
 * This interface provides methods for querying and managing user data
 * using Spring Data JPA. It supports finding users by email or username,
 * and checking for existing usernames or emails for registration validation.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address (case-insensitive).
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty otherwise
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Finds a user by their username (case-insensitive).
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty otherwise
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * Checks if a user with the given username already exists (case-sensitive).
     * Useful for validating uniqueness during signup or updates.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email already exists (case-sensitive).
     * Useful for validating uniqueness during signup or updates.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.articles WHERE u.email = :email")
    Optional<User> findByEmailWithArticles(@Param("email") String email);

}
