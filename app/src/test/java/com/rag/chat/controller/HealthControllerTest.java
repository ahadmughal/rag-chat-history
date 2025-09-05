package com.rag.chat.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HealthControllerTest {

    private HealthController healthController;

    @Mock
    private HealthEndpoint healthEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        healthController = new HealthController(healthEndpoint);
    }

    @Test
    void testHealth_WithHealthInstance() {
        Health health = Health.up()
                .withDetail("db", "up")
                .build();
        when(healthEndpoint.health()).thenReturn(health);
        ResponseEntity<?> response = healthController.health();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("UP", responseBody.get("status"));
        assertEquals(health.getDetails(), responseBody.get("details"));
        verify(healthEndpoint, times(1)).health();
    }

    @Test
    void testHealth_WithoutHealthInstance() {
        HealthComponent healthComponent = mock(HealthComponent.class);
        when(healthComponent.getStatus()).thenReturn(new org.springframework.boot.actuate.health.Status("DOWN"));
        when(healthEndpoint.health()).thenReturn(healthComponent);
        ResponseEntity<?> response = healthController.health();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("DOWN", responseBody.get("status"));
        verify(healthEndpoint, times(1)).health();
    }
}