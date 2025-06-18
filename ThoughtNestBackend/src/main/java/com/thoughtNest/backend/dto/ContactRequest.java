package com.thoughtNest.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO representing the data received from the contact form.
 * Validated using Jakarta Bean Validation annotations.
 */
public class ContactRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must be under 1000 characters")
    private String message;

    // Constructors

    public ContactRequest() {}

    public ContactRequest(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    // Getters and setters

    /**
     * @return The name of the sender.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the sender.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The sender's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The sender's email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The message content from the contact form.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message content from the contact form.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
