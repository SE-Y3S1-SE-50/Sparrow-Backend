package com.sparrow.parcel_service.dto;

import com.sparrow.parcel_service.model.ParcelStatus;
import lombok.Data;

import java.math.BigDecimal;

/** All fields optional; only non-null values will be applied. */
@Data
public class ParcelUpdateRequest {
    private String senderName;
    private String receiverName;
    private String origin;
    private String destination;
    private BigDecimal weightKg;     // optional
    private String dimensions;       // optional "LxWxH cm"
    private ParcelStatus status;     // optional
}
