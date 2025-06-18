package com.thoughtNest.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a contact message submitted by a user through the contact form.
 * This entity stores the sender's name, email, and message content.
 */
@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    /**
     * Primary key for the contact message.
     * Auto-generated using IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the person submitting the contact form.
     * Cannot be blank.
     */
    @NotBlank
    private String name;

    /**
     * Email address of the sender.
     * Must be a valid email format and not blank.
     */
    @Email
    @NotBlank
    private String email;

    /**
     * The message content submitted by the user.
     * Cannot be blank and is limited to 1000 characters.
     */
    @NotBlank
    @Column(length = 1000)
    private String message;

    /**
     * Default constructor.
     */
    public ContactMessage() {}

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
