package com.sparrow.consolidation_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidationRequest {
    
    @NotBlank(message = "Consolidation type is required")
    private String type; // ZIP_CODE, CITY, STATE, REGION, CUSTOM
    
    @NotBlank(message = "Destination ZIP is required")
    private String destinationZip;
    
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    
    @NotNull(message = "Parcel IDs are required")
    private List<String> parcelIds;
    
    private String warehouseId;
    private String assignedDriver;
    private String assignedVehicle;
    private String notes;
}

