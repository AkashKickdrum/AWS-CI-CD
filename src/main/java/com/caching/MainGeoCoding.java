package com.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for the Geocoding application.
 * It serves as the entry point to bootstrap the Spring Boot application.
 */
@SpringBootApplication
@EnableCaching
public class MainGeoCoding {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MainGeoCoding.class, args);
    }
}
