package com.sparrow.consolidation_service.controller;

import com.sparrow.consolidation_service.dto.ConsolidationRequest;
import com.sparrow.consolidation_service.dto.ConsolidationResponse;
import com.sparrow.consolidation_service.service.ConsolidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consolidation")
@RequiredArgsConstructor
@Slf4j
public class ConsolidationController {
    
    private final ConsolidationService consolidationService;
    
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ConsolidationResponse> createConsolidationGroup(@Valid @RequestBody ConsolidationRequest request) {
        log.info("Creating consolidation group for destination: {}", request.getDestinationZip());
        ConsolidationResponse response = consolidationService.createConsolidationGroup(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConsolidationResponse> getConsolidationGroup(@PathVariable String groupId) {
        log.info("Getting consolidation group: {}", groupId);
        ConsolidationResponse response = consolidationService.getConsolidationGroup(groupId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/destination/{destinationZip}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ConsolidationResponse>> getConsolidationsByDestination(@PathVariable String destinationZip) {
        log.info("Getting consolidations for destination ZIP: {}", destinationZip);
        List<ConsolidationResponse> responses = consolidationService.getConsolidationsByDestination(destinationZip);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ConsolidationResponse>> getConsolidationsByStatus(@PathVariable String status) {
        log.info("Getting consolidations with status: {}", status);
        List<ConsolidationResponse> responses = consolidationService.getConsolidationsByStatus(status);
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{groupId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DRIVER')")
    public ResponseEntity<ConsolidationResponse> updateConsolidationStatus(
            @PathVariable String groupId, 
            @PathVariable String status) {
        log.info("Updating consolidation group {} status to: {}", groupId, status);
        ConsolidationResponse response = consolidationService.updateConsolidationStatus(groupId, status);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/auto-consolidate/zip/{destinationZip}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ConsolidationResponse>> autoConsolidateByZip(@PathVariable String destinationZip) {
        log.info("Auto-consolidating parcels for ZIP: {}", destinationZip);
        List<ConsolidationResponse> responses = consolidationService.autoConsolidateByZip(destinationZip);
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/auto-consolidate/city/{destinationCity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ConsolidationResponse>> autoConsolidateByCity(@PathVariable String destinationCity) {
        log.info("Auto-consolidating parcels for city: {}", destinationCity);
        List<ConsolidationResponse> responses = consolidationService.autoConsolidateByCity(destinationCity);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "consolidation-service",
                "timestamp", System.currentTimeMillis()
        ));
    }
}

