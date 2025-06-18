package com.thoughtNest.backend.service;

import com.thoughtNest.backend.model.ContactMessage;
import com.thoughtNest.backend.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class to handle contact form submissions.
 * Responsible for saving messages and sending email notifications.
 */
@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Saves the contact message to the database and sends an email notification.
     *
     * @param message the contact message submitted by the user
     * @return the saved ContactMessage object
     */
    public ContactMessage saveMessage(ContactMessage message) {
        // Save the message to the database
        contactMessageRepository.save(message);

        // Send an email notification to site admin
        sendEmailNotification(message);

        return message;
    }

    /**
     * Sends a simple email to notify about the new contact message.
     *
     * @param message the contact message data
     */
    private void sendEmailNotification(ContactMessage message) {
        // Construct a simple email message
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("harineeanandh@gmail.com"); // Replace with your actual admin email
        mail.setSubject("New Contact Message from " + message.getName());
        mail.setText(
                "Name: " + message.getName() +
                        "\nEmail: " + message.getEmail() +
                        "\n\nMessage:\n" + message.getMessage()
        );

        // Send the email
        mailSender.send(mail);
    }

}
