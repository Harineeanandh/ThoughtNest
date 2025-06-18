package com.thoughtNest.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JwtUtil class.
 *
 * This class verifies the correctness of JWT generation, validation,
 * username extraction, and expiration handling.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    /**
     * Initializes JwtUtil and injects a test secret key using reflection.
     *
     * @throws Exception if reflection fails to access or set the field
     */
    @BeforeEach
    void setup() throws Exception {
        jwtUtil = new JwtUtil();

        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "mySecretKey1236784676454789474737498373979387374774938739743983989383");
    }

    /**
     * Verifies that a token generated with a specific username can be validated
     * and the correct username can be extracted.
     */
    @Test
    void generateAndValidateToken() {
        String token = jwtUtil.generateToken("testUser");

        assertNotNull(token);
        assertEquals("testUser", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, "testUser"));
    }

    /**
     * Verifies that validation fails when the token is used for a different username.
     */
    @Test
    void tokenFailsForDifferentUser() {
        String token = jwtUtil.generateToken("user1");

        assertFalse(jwtUtil.validateToken(token, "user2"));
    }

    /**
     * Verifies that a valid expiration date is extracted from the generated token.
     */
    @Test
    void extractExpiration() {
        String token = jwtUtil.generateToken("testUser");

        assertNotNull(jwtUtil.extractExpiration(token));
    }
}
