package com.sparrow.consolidation_service.service;

import com.sparrow.consolidation_service.dto.*;
import com.sparrow.consolidation_service.model.ConsolidationGroup;
import com.sparrow.consolidation_service.model.ParcelConsolidation;
import com.sparrow.consolidation_service.repository.ConsolidationGroupRepository;
import com.sparrow.consolidation_service.repository.ParcelConsolidationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsolidationService {
    
    private final ConsolidationGroupRepository consolidationGroupRepository;
    private final ParcelConsolidationRepository parcelConsolidationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    public ConsolidationResponse createConsolidationGroup(ConsolidationRequest request) {
        log.info("Creating consolidation group for destination: {}", request.getDestinationZip());
        
        // Generate unique group ID
        String groupId = generateGroupId(request.getType(), request.getDestinationZip());
        
        // Create consolidation group
        ConsolidationGroup group = ConsolidationGroup.builder()
                .groupId(groupId)
                .type(ConsolidationGroup.ConsolidationType.valueOf(request.getType()))
                .destinationZip(request.getDestinationZip())
                .destinationCity(request.getDestinationCity())
                .destinationState(request.getDestinationState())
                .destinationCountry(request.getDestinationCountry())
                .parcelIds(request.getParcelIds())
                .trackingNumbers(new ArrayList<>()) // Will be populated from parcel data
                .status(ConsolidationGroup.ConsolidationStatus.PENDING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .assignedDriver(request.getAssignedDriver())
                .assignedVehicle(request.getAssignedVehicle())
                .warehouseId(request.getWarehouseId())
                .notes(request.getNotes())
                .build();
        
        // Calculate totals (this would typically fetch from parcel service)
        calculateGroupTotals(group);
        
        ConsolidationGroup savedGroup = consolidationGroupRepository.save(group);
        
        // Create parcel consolidation records
        createParcelConsolidations(savedGroup, request.getParcelIds());
        
        // Publish consolidation event
        publishConsolidationEvent(savedGroup);
        
        return buildConsolidationResponse(savedGroup);
    }
    
    public ConsolidationResponse getConsolidationGroup(String groupId) {
        log.info("Getting consolidation group: {}", groupId);
        
        ConsolidationGroup group = consolidationGroupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Consolidation group not found: " + groupId));
        
        return buildConsolidationResponse(group);
    }
    
    public List<ConsolidationResponse> getConsolidationsByDestination(String destinationZip) {
        log.info("Getting consolidations for destination ZIP: {}", destinationZip);
        
        List<ConsolidationGroup> groups = consolidationGroupRepository.findByDestinationZip(destinationZip);
        
        return groups.stream()
                .map(this::buildConsolidationResponse)
                .collect(Collectors.toList());
    }
    
    public List<ConsolidationResponse> getConsolidationsByStatus(String status) {
        log.info("Getting consolidations with status: {}", status);
        
        ConsolidationGroup.ConsolidationStatus consolidationStatus = 
                ConsolidationGroup.ConsolidationStatus.valueOf(status.toUpperCase());
        
        List<ConsolidationGroup> groups = consolidationGroupRepository.findByStatus(consolidationStatus);
        
        return groups.stream()
                .map(this::buildConsolidationResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ConsolidationResponse updateConsolidationStatus(String groupId, String status) {
        log.info("Updating consolidation group {} status to: {}", groupId, status);
        
        ConsolidationGroup group = consolidationGroupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Consolidation group not found: " + groupId));
        
        group.setStatus(ConsolidationGroup.ConsolidationStatus.valueOf(status.toUpperCase()));
        group.setUpdatedAt(Instant.now());
        
        ConsolidationGroup savedGroup = consolidationGroupRepository.save(group);
        
        // Update parcel consolidation statuses
        updateParcelConsolidationStatuses(groupId, status);
        
        // Publish status update event
        publishStatusUpdateEvent(savedGroup);
        
        return buildConsolidationResponse(savedGroup);
    }
    
    public List<ConsolidationResponse> autoConsolidateByZip(String destinationZip) {
        log.info("Auto-consolidating parcels for ZIP: {}", destinationZip);
        
        // Find pending parcels for the destination ZIP
        List<ParcelConsolidation> pendingParcels = parcelConsolidationRepository
                .findByDestinationZipAndStatus(destinationZip, ParcelConsolidation.ConsolidationStatus.PENDING_CONSOLIDATION);
        
        if (pendingParcels.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Group parcels by similar characteristics
        Map<String, List<ParcelConsolidation>> groupedParcels = groupParcelsByCharacteristics(pendingParcels);
        
        List<ConsolidationResponse> consolidations = new ArrayList<>();
        
        for (Map.Entry<String, List<ParcelConsolidation>> entry : groupedParcels.entrySet()) {
            List<ParcelConsolidation> parcels = entry.getValue();
            
            if (parcels.size() >= 2) { // Only consolidate if there are at least 2 parcels
                ConsolidationRequest request = ConsolidationRequest.builder()
                        .type("ZIP_CODE")
                        .destinationZip(destinationZip)
                        .destinationCity(parcels.get(0).getDestinationCity())
                        .destinationState(parcels.get(0).getDestinationState())
                        .destinationCountry(parcels.get(0).getDestinationCountry())
                        .parcelIds(parcels.stream().map(ParcelConsolidation::getParcelId).collect(Collectors.toList()))
                        .build();
                
                ConsolidationResponse consolidation = createConsolidationGroup(request);
                consolidations.add(consolidation);
            }
        }
        
        return consolidations;
    }
    
    public List<ConsolidationResponse> autoConsolidateByCity(String destinationCity) {
        log.info("Auto-consolidating parcels for city: {}", destinationCity);
        
        List<ParcelConsolidation> pendingParcels = parcelConsolidationRepository
                .findByDestinationCityAndStatus(destinationCity, ParcelConsolidation.ConsolidationStatus.PENDING_CONSOLIDATION);
        
        if (pendingParcels.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Group by ZIP codes within the city
        Map<String, List<ParcelConsolidation>> groupedByZip = pendingParcels.stream()
                .collect(Collectors.groupingBy(ParcelConsolidation::getDestinationZip));
        
        List<ConsolidationResponse> consolidations = new ArrayList<>();
        
        for (Map.Entry<String, List<ParcelConsolidation>> entry : groupedByZip.entrySet()) {
            String zipCode = entry.getKey();
            List<ParcelConsolidation> parcels = entry.getValue();
            
            if (parcels.size() >= 2) {
                ConsolidationRequest request = ConsolidationRequest.builder()
                        .type("CITY")
                        .destinationZip(zipCode)
                        .destinationCity(destinationCity)
                        .destinationState(parcels.get(0).getDestinationState())
                        .destinationCountry(parcels.get(0).getDestinationCountry())
                        .parcelIds(parcels.stream().map(ParcelConsolidation::getParcelId).collect(Collectors.toList()))
                        .build();
                
                ConsolidationResponse consolidation = createConsolidationGroup(request);
                consolidations.add(consolidation);
            }
        }
        
        return consolidations;
    }
    
    private void createParcelConsolidations(ConsolidationGroup group, List<String> parcelIds) {
        for (String parcelId : parcelIds) {
            // In a real implementation, you would fetch parcel details from the parcel service
            ParcelConsolidation parcelConsolidation = ParcelConsolidation.builder()
                    .parcelId(parcelId)
                    .trackingNumber("TRK-" + parcelId) // This would come from parcel service
                    .consolidationGroupId(group.getId())
                    .destinationZip(group.getDestinationZip())
                    .destinationCity(group.getDestinationCity())
                    .destinationState(group.getDestinationState())
                    .destinationCountry(group.getDestinationCountry())
                    .status(ParcelConsolidation.ConsolidationStatus.CONSOLIDATED)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            parcelConsolidationRepository.save(parcelConsolidation);
        }
    }
    
    private void calculateGroupTotals(ConsolidationGroup group) {
        // In a real implementation, you would fetch parcel details and calculate totals
        group.setTotalWeight(BigDecimal.valueOf(0));
        group.setTotalVolume(BigDecimal.valueOf(0));
        group.setParcelCount(group.getParcelIds().size());
    }
    
    private void updateParcelConsolidationStatuses(String groupId, String status) {
        List<ParcelConsolidation> parcelConsolidations = parcelConsolidationRepository
                .findByConsolidationGroupId(groupId);
        
        ParcelConsolidation.ConsolidationStatus newStatus = 
                ParcelConsolidation.ConsolidationStatus.valueOf(status.toUpperCase());
        
        for (ParcelConsolidation pc : parcelConsolidations) {
            pc.setStatus(newStatus);
            pc.setUpdatedAt(Instant.now());
            parcelConsolidationRepository.save(pc);
        }
    }
    
    private Map<String, List<ParcelConsolidation>> groupParcelsByCharacteristics(List<ParcelConsolidation> parcels) {
        // Group by destination ZIP for now - could be enhanced with more sophisticated grouping
        return parcels.stream()
                .collect(Collectors.groupingBy(ParcelConsolidation::getDestinationZip));
    }
    
    private String generateGroupId(String type, String destinationZip) {
        return "CONS-" + type + "-" + destinationZip + "-" + System.currentTimeMillis();
    }
    
    private void publishConsolidationEvent(ConsolidationGroup group) {
        try {
            kafkaTemplate.send("consolidation-events", group.getGroupId(), group);
            log.info("Published consolidation event for group: {}", group.getGroupId());
        } catch (Exception e) {
            log.error("Failed to publish consolidation event", e);
        }
    }
    
    private void publishStatusUpdateEvent(ConsolidationGroup group) {
        try {
            kafkaTemplate.send("consolidation-status-updates", group.getGroupId(), group);
            log.info("Published status update event for group: {}", group.getGroupId());
        } catch (Exception e) {
            log.error("Failed to publish status update event", e);
        }
    }
    
    private ConsolidationResponse buildConsolidationResponse(ConsolidationGroup group) {
        List<ParcelConsolidation> parcelConsolidations = parcelConsolidationRepository
                .findByConsolidationGroupId(group.getId());
        
        List<ConsolidationResponse.ParcelInfo> parcelInfos = parcelConsolidations.stream()
                .map(this::buildParcelInfo)
                .collect(Collectors.toList());
        
        return ConsolidationResponse.builder()
                .id(group.getId())
                .groupId(group.getGroupId())
                .type(group.getType().name())
                .destinationZip(group.getDestinationZip())
                .destinationCity(group.getDestinationCity())
                .destinationState(group.getDestinationState())
                .destinationCountry(group.getDestinationCountry())
                .parcelIds(group.getParcelIds())
                .trackingNumbers(group.getTrackingNumbers())
                .totalWeight(group.getTotalWeight())
                .totalVolume(group.getTotalVolume())
                .parcelCount(group.getParcelCount())
                .status(group.getStatus().name())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .scheduledPickup(group.getScheduledPickup())
                .scheduledDelivery(group.getScheduledDelivery())
                .assignedDriver(group.getAssignedDriver())
                .assignedVehicle(group.getAssignedVehicle())
                .warehouseId(group.getWarehouseId())
                .notes(group.getNotes())
                .estimatedCost(group.getEstimatedCost())
                .actualCost(group.getActualCost())
                .parcels(parcelInfos)
                .build();
    }
    
    private ConsolidationResponse.ParcelInfo buildParcelInfo(ParcelConsolidation pc) {
        return ConsolidationResponse.ParcelInfo.builder()
                .parcelId(pc.getParcelId())
                .trackingNumber(pc.getTrackingNumber())
                .originZip(pc.getOriginZip())
                .destinationZip(pc.getDestinationZip())
                .weight(pc.getWeight())
                .volume(pc.getVolume())
                .status(pc.getStatus().name())
                .build();
    }
}

