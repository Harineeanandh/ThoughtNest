package com.thoughtNest.backend.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for generating consistent API responses.
 * All responses follow a standard format containing:
 * - message: brief description
 * - status: HTTP status code
 * - data: response payload (if any)
 */
public class ResponseHandler {

    /**
     * Generic method to build a full ResponseEntity with message, status code, and optional data.
     */
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("message", message);             // Custom message from controller or service
        map.put("status", status.value());       // Numeric HTTP status (e.g., 200, 404)
        map.put("data", responseObj);            // Payload (could be null for errors)

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Explicitly set JSON content type

        return new ResponseEntity<>(map, headers, status);
    }

    /**
     * Shortcut method to return a 200 OK response with message and data.
     */
    public static ResponseEntity<Object> success(String message, Object data) {
        return generateResponse(message, HttpStatus.OK, data);
    }

    /**
     * Shortcut method to return a 200 OK response with only a message and no payload.
     */
    public static ResponseEntity<Object> success(String message) {
        return generateResponse(message, HttpStatus.OK, null);
    }

    /**
     * Shortcut method to return an error response with message and custom status code.
     */
    public static ResponseEntity<Object> error(String message, HttpStatus status) {
        return generateResponse(message, status, null);
    }
}
