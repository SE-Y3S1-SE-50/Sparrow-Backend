package com.sparrow.tracking_service.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.sparrow.tracking_service.dto.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsService {
    
    @Value("${google.maps.api-key}")
    private String apiKey;
    
    private GeoApiContext context;
    
    private GeoApiContext getContext() {
        if (context == null) {
            context = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();
        }
        return context;
    }
    
    public GeocodingResponse reverseGeocode(Double latitude, Double longitude) {
        try {
            LatLng latLng = new LatLng(latitude, longitude);
            GeocodingResult[] results = GeocodingApi.reverseGeocode(getContext(), latLng).await();
            
            if (results.length > 0) {
                GeocodingResult result = results[0];
                return GeocodingResponse.builder()
                        .formattedAddress(result.formattedAddress)
                        .placeId(result.placeId)
                        .addressComponents(extractAddressComponents(result))
                        .build();
            }
            
            return GeocodingResponse.builder()
                    .formattedAddress("Unknown Location")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in reverse geocoding for lat: {}, lng: {}", latitude, longitude, e);
            return GeocodingResponse.builder()
                    .formattedAddress("Error getting address")
                    .build();
        }
    }
    
    public GeocodingResponse geocode(String address) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(getContext(), address).await();
            
            if (results.length > 0) {
                GeocodingResult result = results[0];
                return GeocodingResponse.builder()
                        .formattedAddress(result.formattedAddress)
                        .placeId(result.placeId)
                        .latitude(result.geometry.location.lat)
                        .longitude(result.geometry.location.lng)
                        .addressComponents(extractAddressComponents(result))
                        .build();
            }
            
            return GeocodingResponse.builder()
                    .formattedAddress("Address not found")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in geocoding for address: {}", address, e);
            return GeocodingResponse.builder()
                    .formattedAddress("Error geocoding address")
                    .build();
        }
    }
    
    private List<GeocodingResponse.AddressComponent> extractAddressComponents(GeocodingResult result) {
        return Arrays.stream(result.addressComponents)
                .map(component -> GeocodingResponse.AddressComponent.builder()
                        .longName(component.longName)
                        .shortName(component.shortName)
                        .types(Arrays.stream(component.types)
                                .map(Enum::name)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}

