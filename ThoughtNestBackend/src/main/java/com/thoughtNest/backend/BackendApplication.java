package com.thoughtNest.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the Spring Boot application.
 * The @SpringBootApplication annotation enables:
 * - Component scanning
 * - Auto-configuration
 * - Configuration bean support
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Launches the Spring Boot application.
     * This triggers the entire Spring context to start up.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
