package com.sparrow.tracking_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

@Document(collection = "tracking_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEvent {
    
    @Id
    private String id;
    
    @Indexed
    private String parcelId;
    
    @Indexed
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
    
    private Instant timestamp;
    private String driverId;
    private String vehicleId;
    private String notes;
    
    // Google Maps specific fields
    private String placeId;
    private String formattedAddress;
    private Double accuracy; // GPS accuracy in meters
}

