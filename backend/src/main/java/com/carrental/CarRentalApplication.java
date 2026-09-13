package com.carrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for the Car Rental Application.
 * Modular Monolith Architecture with Spring Boot and PostgreSQL.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.carrental")
public class CarRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
    }
}
