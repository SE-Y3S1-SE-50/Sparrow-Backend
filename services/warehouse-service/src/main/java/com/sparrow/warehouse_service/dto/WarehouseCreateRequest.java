package com.sparrow.warehouse_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WarehouseCreateRequest {
    @NotBlank @Size(min = 3, max = 50)
    private String name;

    @NotBlank @Size(min = 3, max = 120)
    private String address;

    @NotBlank @Size(min = 2, max = 60)
    private String city;

    @NotBlank @Size(min = 2, max = 60)
    private String country;

    @Min(0)
    private Integer capacity;

    private Boolean active;



}