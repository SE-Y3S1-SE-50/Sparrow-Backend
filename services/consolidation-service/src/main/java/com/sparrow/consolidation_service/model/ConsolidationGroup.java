package com.sparrow.consolidation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "consolidation_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidationGroup {
    
    @Id
    private String id;
    
    @Indexed
    private String groupId;
    
    private ConsolidationType type;
    private String destinationZip;
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    
    private List<String> parcelIds;
    private List<String> trackingNumbers;
    
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;
    private Integer parcelCount;
    
    private ConsolidationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant scheduledPickup;
    private Instant scheduledDelivery;
    
    private String assignedDriver;
    private String assignedVehicle;
    private String warehouseId;
    
    private String notes;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    
    public enum ConsolidationType {
        ZIP_CODE,
        CITY,
        STATE,
        REGION,
        CUSTOM
    }
    
    public enum ConsolidationStatus {
        PENDING,
        IN_PROGRESS,
        READY_FOR_PICKUP,
        IN_TRANSIT,
        DELIVERED,
        CANCELLED
    }
}
