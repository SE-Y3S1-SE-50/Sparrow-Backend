package com.sparrow.warehouse_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class WarehouseUpdateRequest {
    @Size(min = 3, max = 50)
    private String name;

    @Size(min = 3, max = 120)
    private String address;

    @Size(min = 2, max = 60)
    private String city;

    @Size(min = 2, max = 60)
    private String country;

    @Min(0)
    private Integer capacity;

    private Boolean active;


}