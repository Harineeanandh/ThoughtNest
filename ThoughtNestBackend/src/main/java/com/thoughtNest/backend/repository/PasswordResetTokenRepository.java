package com.thoughtNest.backend.repository;

import com.thoughtNest.backend.model.PasswordResetToken;
import com.thoughtNest.backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for {@link PasswordResetToken} entities.
 *
 * This interface provides convenient access to the database for managing password reset tokens,
 * leveraging Spring Data JPA's built-in functionalities. It includes custom methods for
 * querying and modifying password reset records based on token strings and associated users.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Finds a PasswordResetToken by the token string.
     * This method is used during password reset workflows when a user clicks a reset link.
     *
     * @param token the token string to look up
     * @return an Optional containing the token if found, or empty otherwise
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Deletes the PasswordResetToken associated with a specific user ID.
     * This is useful for ensuring that old or used reset tokens are removed,
     * especially after a successful password reset or when issuing a new token.
     *
     * The method is annotated with:
     * - @Modifying: indicates that the query modifies the database (not a SELECT)
     * - @Transactional: ensures the operation runs within a transaction to maintain data integrity
     *
     * @param userId the ID of the user whose token should be deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.user.id = :userId")
    void deleteByUser(@Param("userId") Long userId);
}
