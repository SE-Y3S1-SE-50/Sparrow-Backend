package com.sparrow.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RbacDemoController {
    @GetMapping("/admin/only")    public ResponseEntity<?> adminOnly()    { return ResponseEntity.ok(Map.of("ok", true, "who", "ADMIN")); }
    @GetMapping("/driver/only")   public ResponseEntity<?> driverOnly()   { return ResponseEntity.ok(Map.of("ok", true, "who", "DRIVER or ADMIN")); }
    @GetMapping("/staff/only")    public ResponseEntity<?> staffOnly()    { return ResponseEntity.ok(Map.of("ok", true, "who", "STAFF or ADMIN")); }
    @GetMapping("/customer/only") public ResponseEntity<?> customerOnly() { return ResponseEntity.ok(Map.of("ok", true, "who", "CUSTOMER or ADMIN")); }
}
