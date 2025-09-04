package com.sparrow.tracking_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponse {
    
    private String trackingNumber;
    private String status;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String formattedAddress;
    private Instant timestamp;
    private String driverId;
    private String vehicleId;
    private String notes;
    private Double accuracy;
    private List<TrackingEventResponse> history;
    
    // Google Maps specific fields for frontend
    private String placeId;
    private String mapUrl;
    private String staticMapUrl;
    private Double estimatedDeliveryTime; // in minutes
    private String currentLocation;
    private Boolean isDelivered;
    private String deliveryStatus;
}

