package com.thoughtNest.backend;

import com.thoughtNest.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the EmailService class.
 *
 * This test verifies the behavior of the sendPasswordResetEmail method,
 * including successful email sending through a mocked JavaMailSender.
 */
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    /**
     * Initializes mocks and sets up the default behavior for the mail sender.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    /**
     * Verifies that the sendPasswordResetEmail method executes without
     * throwing exceptions and that the appropriate methods on the mail sender
     * are invoked.
     *
     * @throws MessagingException if there is an error composing the message
     */
    @Test
    void testSendPasswordResetEmail_sendsSuccessfully() throws MessagingException {
        String email = "test@example.com";
        String token = "token123";

        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(email, token));

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}
