package com.sparrow.tracking_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingResponse {
    
    private String formattedAddress;
    private String placeId;
    private Double latitude;
    private Double longitude;
    private List<AddressComponent> addressComponents;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressComponent {
        private String longName;
        private String shortName;
        private List<String> types;
    }
}

