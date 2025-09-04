package com.sparrow.consolidation_service.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ConsolidationResponse {
    
    private String id;
    private String groupId;
    private String type;
    private String destinationZip;
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    
    private List<String> parcelIds;
    private List<String> trackingNumbers;
    
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;
    private Integer parcelCount;
    
    private String status;
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
    
    private List<ParcelInfo> parcels;
    
    @Data
    @Builder
    public static class ParcelInfo {
        private String parcelId;
        private String trackingNumber;
        private String senderName;
        private String receiverName;
        private String originZip;
        private String destinationZip;
        private BigDecimal weight;
        private BigDecimal volume;
        private String status;
    }
}

