package com.sparrow.parcel_service.dto;

import com.sparrow.parcel_service.model.ParcelStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder

public class ParcelResponse {
    String id;
    String trackingNumber;
    String senderName;
    String receiverName;
    String origin;
    String destination;
    BigDecimal weightKg;
    String dimensions;
    ParcelStatus status;
    Instant createdAt;
    Instant updatedAt;
}


