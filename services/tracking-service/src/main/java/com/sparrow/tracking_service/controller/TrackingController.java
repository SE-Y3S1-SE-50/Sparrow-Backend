package com.sparrow.tracking_service.controller;

import com.sparrow.tracking_service.dto.TrackingResponse;
import com.sparrow.tracking_service.dto.TrackingUpdateRequest;
import com.sparrow.tracking_service.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Slf4j
public class TrackingController {
    
    private final TrackingService trackingService;
    
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'STAFF')")
    public ResponseEntity<TrackingResponse> updateTracking(@Valid @RequestBody TrackingUpdateRequest request) {
        log.info("Received tracking update request for: {}", request.getTrackingNumber());
        TrackingResponse response = trackingService.updateTracking(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{trackingNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TrackingResponse> getTrackingInfo(@PathVariable String trackingNumber) {
        log.info("Getting tracking info for: {}", trackingNumber);
        TrackingResponse response = trackingService.getTrackingInfo(trackingNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'STAFF')")
    public ResponseEntity<List<TrackingResponse>> getTrackingByDriver(@PathVariable String driverId) {
        log.info("Getting tracking info for driver: {}", driverId);
        List<TrackingResponse> responses = trackingService.getTrackingByDriver(driverId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<TrackingResponse>> getTrackingByLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        log.info("Getting tracking info near location: {}, {} within {}km", latitude, longitude, radiusKm);
        List<TrackingResponse> responses = trackingService.getTrackingByLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "tracking-service",
                "timestamp", System.currentTimeMillis()
        ));
    }
}

