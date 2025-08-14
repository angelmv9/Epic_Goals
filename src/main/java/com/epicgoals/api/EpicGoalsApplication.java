// ABOUT_ME: Main Spring Boot application class for Epic Goals habit tracking system
// ABOUT_ME: Configures the application context and starts the embedded web server
package com.epicgoals.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EpicGoalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpicGoalsApplication.class, args);
    }

}