package com.sparrow.tracking_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventResponse {
    private String id;
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
    private String placeId;
    private String formattedAddress;
}
