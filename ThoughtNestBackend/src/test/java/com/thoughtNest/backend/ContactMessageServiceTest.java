package com.thoughtNest.backend;

import com.thoughtNest.backend.model.ContactMessage;
import com.thoughtNest.backend.repository.ContactMessageRepository;
import com.thoughtNest.backend.service.ContactMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for ContactMessageService.
 *
 * This test ensures that a contact message is correctly saved
 * and that an email notification is sent when a message is received.
 */
class ContactMessageServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ContactMessageService contactMessageService;

    private ContactMessage message;

    /**
     * Initializes mocks and prepares a test message before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample contact message for testing
        message = new ContactMessage();
        message.setId(1L);
        message.setName("Jane Doe");
        message.setEmail("jane@example.com");
        message.setMessage("Test message");
    }

    /**
     * Tests the saving of a contact message and sending an email notification.
     *
     * Verifies:
     * - The message is saved to the repository.
     * - An email is sent to the designated recipient.
     * - The content of the email contains the submitted contact data.
     */
    @Test
    void testSaveMessageAndSendEmail() {
        // Mock repository to return the same message on save
        when(contactMessageRepository.save(message)).thenReturn(message);

        // Call the method under test
        ContactMessage saved = contactMessageService.saveMessage(message);

        // Verify the saved message matches the input
        assertEquals(message, saved);

        // Ensure repository save was called
        verify(contactMessageRepository).save(message);

        // Capture the email sent by the mail sender
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(emailCaptor.capture());

        // Validate email content
        SimpleMailMessage email = emailCaptor.getValue();
        assertEquals("harineeanandh@gmail.com", email.getTo()[0]);
        assertEquals("New Contact Message from Jane Doe", email.getSubject());
        assertTrue(email.getText().contains("Name: Jane Doe"));
        assertTrue(email.getText().contains("Email: jane@example.com"));
        assertTrue(email.getText().contains("Message:\nTest message"));
    }
}
