package com.thoughtNest.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service class responsible for sending emails (e.g., password reset emails).
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Constructor-based injection for JavaMailSender.
     *
     * @param mailSender the mail sender bean configured in application context
     */
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a password reset email with a secure link containing the reset token.
     *
     * @param toEmail the recipient's email address
     * @param token   the password reset token
     * @throws MessagingException if sending the email fails
     */
    public void sendPasswordResetEmail(String toEmail, String token) throws MessagingException {
        // Subject of the email
        String subject = "Password Reset Request - ThoughtNest";

        // Link that user will click to reset password
        String resetLink = "https://thoughnest-frontend.vercel.app/reset-password?token=" + token;

        // HTML content of the email
        String body = "<p>Hello,</p>" +
                "<p>You requested a password reset. Click the link below to reset your password:</p>" +
                "<p><a href=\"" + resetLink + "\">Reset Password</a></p>" +
                "<p>This link will expire in 30 minutes.</p>" +
                "<p>If you did not request this, please ignore this email.</p>";

        // Create a MIME-style email (supports HTML)
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set email properties
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // true = enable HTML content

        // Send the email
        mailSender.send(message);
    }
}
