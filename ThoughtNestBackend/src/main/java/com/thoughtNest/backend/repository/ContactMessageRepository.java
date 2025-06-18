package com.thoughtNest.backend.repository;

import com.thoughtNest.backend.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on {@link ContactMessage} entities.
 * Inherits basic methods like save, findById, findAll, deleteById, etc., from {@link JpaRepository}.
 */
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    // No custom methods required as of now — default CRUD methods from JpaRepository are sufficient.
}
