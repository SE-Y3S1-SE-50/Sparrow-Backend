package com.sparrow.tracking_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.time.Instant;

@Document(collection = "parcel_locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelLocation {
    
    @Id
    private String id;
    
    @Indexed
    private String parcelId;
    
    @Indexed
    private String trackingNumber;
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] coordinates; // [longitude, latitude]
    
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String placeId;
    private String formattedAddress;
    
    private Instant timestamp;
    private Double accuracy; // GPS accuracy in meters
    private String status;
    private String driverId;
    private String vehicleId;
    
    // Helper method to set coordinates from lat/lng
    public void setCoordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.coordinates = new double[]{longitude, latitude};
    }
}

