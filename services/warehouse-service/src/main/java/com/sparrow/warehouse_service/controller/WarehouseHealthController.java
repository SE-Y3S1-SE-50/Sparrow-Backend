package com.sparrow.warehouse_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseHealthController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("ok", true, "service", "warehouse"));
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping(Authentication auth) {
        return ResponseEntity.ok (
                java.util.Map.of(
                        "ok", true,
                        "user", auth.getName()
                )
        );
    }
}
