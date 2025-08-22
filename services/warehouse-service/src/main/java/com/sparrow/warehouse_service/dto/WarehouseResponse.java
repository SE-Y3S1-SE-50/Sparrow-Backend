package com.sparrow.warehouse_service.dto;

import lombok.Builder;
import lombok.Value;


import java.time.Instant;

@Value
@Builder

public class WarehouseResponse {
    String id;
    String name;
    String address;
    String city;
    String country;
    Integer capacity;
    Boolean active;
    Instant createdAt;
    Instant updatedAt;
}
