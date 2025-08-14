// ABOUT_ME: Health check endpoint controller for monitoring application status
// ABOUT_ME: Provides a simple endpoint to verify the application is running properly
package com.epicgoals.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        );
        return ResponseEntity.ok(response);
    }
}