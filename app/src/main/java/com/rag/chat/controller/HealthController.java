package com.rag.chat.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        HealthComponent healthComponent = healthEndpoint.health();

        // If the HealthComponent is an instance of Health, we can access details
        if (healthComponent instanceof Health health) {
            Map<String, Object> response = Map.of(
                    "status", health.getStatus().getCode(),
                    "details", health.getDetails()
            );
            return ResponseEntity.ok(response);
        }

        // Fallback for other types of HealthComponent
        return ResponseEntity.ok(Map.of(
                "status", healthComponent.getStatus().getCode()
        ));
    }
}
