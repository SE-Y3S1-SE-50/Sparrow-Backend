package com.sparrow.parcel_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParcelCreateRequest {
    @Size(max = 64)
    private String trackingNumber;

    @NotBlank
    private String senderName;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    private BigDecimal weightKg;
    private String dimensions;
}