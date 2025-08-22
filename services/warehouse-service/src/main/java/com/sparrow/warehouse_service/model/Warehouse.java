package com.sparrow.warehouse_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "warehouses")
@CompoundIndex(name = "uniq_name_city", def = "{'name': 1, 'city': 1}", unique = true)
@Data @NoArgsConstructor @AllArgsConstructor @Builder

public class Warehouse {

    @Id
    private String id;

    private String name;
    private String address;
    @Indexed(name = "city_idx")
    private String city;
    private String country;

    private Integer capacity;
    private Boolean active;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}