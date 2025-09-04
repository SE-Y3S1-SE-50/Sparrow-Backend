package com.sparrow.consolidation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "parcel_consolidations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelConsolidation {
    
    @Id
    private String id;
    
    @Indexed
    private String parcelId;
    
    @Indexed
    private String trackingNumber;
    
    @Indexed
    private String consolidationGroupId;
    
    private String originZip;
    private String originCity;
    private String originState;
    private String originCountry;
    
    private String destinationZip;
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    
    private BigDecimal weight;
    private BigDecimal volume;
    private String dimensions;
    
    private ConsolidationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    
    private String senderName;
    private String receiverName;
    private String senderAddress;
    private String receiverAddress;
    
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    
    public enum ConsolidationStatus {
        PENDING_CONSOLIDATION,
        CONSOLIDATED,
        IN_TRANSIT,
        DELIVERED,
        FAILED
    }
}

