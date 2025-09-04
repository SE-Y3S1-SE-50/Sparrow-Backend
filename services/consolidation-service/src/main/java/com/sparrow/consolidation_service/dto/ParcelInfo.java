package com.sparrow.consolidation_service.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelInfo {
    private String parcelId;
    private String trackingNumber;
    private String senderName;
    private String receiverName;
    private String originZip;
    private String destinationZip;
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    private BigDecimal weight;
    private BigDecimal volume;
    private String dimensions;
    private String status;
    private String senderAddress;
    private String receiverAddress;
}

