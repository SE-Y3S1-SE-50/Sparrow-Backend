package com.sparrow.parcel_service.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "parcels")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Parcel {

    @Id
    private String id;

    @Indexed(unique = true)
    private String trackingNumber;


    private String senderName;
    private String receiverName;
    private String origin;
    private String destination;


    private BigDecimal weightKg;
    private String dimensions;

    private ParcelStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
