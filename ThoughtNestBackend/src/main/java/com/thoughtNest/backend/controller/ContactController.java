package com.thoughtNest.backend.controller;

import com.thoughtNest.backend.dto.ContactRequest;
import com.thoughtNest.backend.model.ContactMessage;
import com.thoughtNest.backend.util.ResponseHandler;
import com.thoughtNest.backend.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles incoming contact messages sent via the website's contact form.
 */
@RestController
@RequestMapping("/api")
public class ContactController {

    @Autowired
    private ContactMessageService contactMessageService;

    /**
     * Accepts and saves a contact message submitted by a user.
     *
     * @param request a validated DTO containing the user's name, email, and message
     * @return a consistent response indicating success
     */
    @PostMapping("/contact")
    public ResponseEntity<Object> sendMessage(@Valid @RequestBody ContactRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setMessage(request.getMessage());

        contactMessageService.saveMessage(message);
        return ResponseHandler.generateResponse("Message sent successfully", HttpStatus.OK, null);
    }
}
