package com.thoughtNest.backend;

import com.thoughtNest.backend.controller.ContactController;
import com.thoughtNest.backend.dto.ContactRequest;
import com.thoughtNest.backend.model.ContactMessage;
import com.thoughtNest.backend.service.ContactMessageService;
import com.thoughtNest.backend.util.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class contains unit tests for the ContactController,
 * which handles contact form submissions from users.
 *
 * The tests verify that contact form data is handled correctly,
 * valid data results in a successful response, and invalid data
 * results in a proper error response.
 */
class ContactControllerTest {

    // Simulates HTTP requests to the controller
    private MockMvc mockMvc;

    // Injects mock dependencies into the controller
    @InjectMocks
    private ContactController contactController;

    // Mocked version of the service used by the controller
    @Mock
    private ContactMessageService contactMessageService;

    // Used to convert Java objects to JSON
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initializes mocks and builds the MockMvc instance with the controller
     * and a global response handler to mimic the actual application behavior.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Set up the controller with global exception/response handling
        mockMvc = MockMvcBuilders.standaloneSetup(contactController)
                .setControllerAdvice(new ResponseHandler())
                .build();
    }

    /**
     * This test simulates a successful contact form submission.
     * It mocks the ContactMessageService to return a valid saved message.
     *
     * Expected behavior:
     * - Status: 200 OK
     * - Response message: "Message sent successfully"
     */
    @Test
    void contact_Success() throws Exception {
        ContactRequest request = new ContactRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setMessage("Hello!");

        // Mocking service to simulate successful saving of message
        when(contactMessageService.saveMessage(any())).thenReturn(new ContactMessage());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message sent successfully"));
    }

    /**
     * This test simulates an invalid contact form submission with missing or incorrect fields.
     * Since validation should fail, we expect a 400 Bad Request response.
     *
     * Expected behavior:
     * - Status: 400 Bad Request
     */
    @Test
    void contact_Failure() throws Exception {
        ContactRequest request = new ContactRequest();
        request.setName(""); // Invalid: name is empty
        request.setEmail("invalid-email"); // Invalid format
        request.setMessage(""); // Invalid: message is empty

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
