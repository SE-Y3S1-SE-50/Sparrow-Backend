package com.sparrow.tracking_service.service;

import com.sparrow.tracking_service.dto.TrackingResponse;
import com.sparrow.tracking_service.dto.TrackingUpdateRequest;
import com.sparrow.tracking_service.dto.GeocodingResponse;
import com.sparrow.tracking_service.dto.TrackingEventResponse;
import com.sparrow.tracking_service.model.TrackingEvent;
import com.sparrow.tracking_service.model.ParcelLocation;
import com.sparrow.tracking_service.repository.TrackingEventRepository;
import com.sparrow.tracking_service.repository.ParcelLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {
    
    private final TrackingEventRepository trackingEventRepository;
    private final ParcelLocationRepository parcelLocationRepository;
    private final GoogleMapsService googleMapsService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${google.maps.api-key}")
    private String apiKey;
    
    @Transactional
    public TrackingResponse updateTracking(TrackingUpdateRequest request) {
        log.info("Updating tracking for: {}", request.getTrackingNumber());
        
        // Get geocoding information from Google Maps
        GeocodingResponse geocoding = googleMapsService.reverseGeocode(
                request.getLatitude(), request.getLongitude());
        
        // Create tracking event
        TrackingEvent event = TrackingEvent.builder()
                .trackingNumber(request.getTrackingNumber())
                .status(request.getStatus())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(geocoding.getFormattedAddress())
                .city(extractCity(geocoding))
                .state(extractState(geocoding))
                .zipCode(extractZipCode(geocoding))
                .country(extractCountry(geocoding))
                .placeId(geocoding.getPlaceId())
                .formattedAddress(geocoding.getFormattedAddress())
                .timestamp(Instant.now())
                .driverId(request.getDriverId())
                .vehicleId(request.getVehicleId())
                .notes(request.getNotes())
                .accuracy(request.getAccuracy())
                .build();
        
        TrackingEvent savedEvent = trackingEventRepository.save(event);
        
        // Update parcel location
        ParcelLocation location = ParcelLocation.builder()
                .trackingNumber(request.getTrackingNumber())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(geocoding.getFormattedAddress())
                .city(extractCity(geocoding))
                .state(extractState(geocoding))
                .zipCode(extractZipCode(geocoding))
                .country(extractCountry(geocoding))
                .placeId(geocoding.getPlaceId())
                .formattedAddress(geocoding.getFormattedAddress())
                .timestamp(Instant.now())
                .accuracy(request.getAccuracy())
                .status(request.getStatus())
                .driverId(request.getDriverId())
                .vehicleId(request.getVehicleId())
                .build();
        
        location.setCoordinates(request.getLatitude(), request.getLongitude());
        parcelLocationRepository.save(location);
        
        // Publish tracking update event to Kafka
        publishTrackingUpdateEvent(savedEvent);
        
        // Get tracking history
        List<TrackingEvent> history = trackingEventRepository
                .findByTrackingNumberOrderByTimestampDesc(request.getTrackingNumber());
        
        return buildTrackingResponse(savedEvent, history);
    }
    
    public TrackingResponse getTrackingInfo(String trackingNumber) {
        log.info("Getting tracking info for: {}", trackingNumber);
        
        TrackingEvent latestEvent = trackingEventRepository
                .findFirstByTrackingNumberOrderByTimestampDesc(trackingNumber)
                .orElseThrow(() -> new IllegalArgumentException("Tracking number not found: " + trackingNumber));
        
        List<TrackingEvent> history = trackingEventRepository
                .findByTrackingNumberOrderByTimestampDesc(trackingNumber);
        
        return buildTrackingResponse(latestEvent, history);
    }
    
    public List<TrackingResponse> getTrackingByDriver(String driverId) {
        log.info("Getting tracking info for driver: {}", driverId);
        
        List<TrackingEvent> events = trackingEventRepository
                .findByDriverIdAndTimestampAfter(driverId, Instant.now().minusSeconds(86400)); // Last 24 hours
        
        return events.stream()
                .collect(Collectors.groupingBy(TrackingEvent::getTrackingNumber))
                .values()
                .stream()
                .map(eventList -> {
                    TrackingEvent latest = eventList.get(0);
                    return buildTrackingResponse(latest, eventList);
                })
                .collect(Collectors.toList());
    }
    
    public List<TrackingResponse> getTrackingByLocation(Double latitude, Double longitude, Double radiusKm) {
        log.info("Getting tracking info near location: {}, {} within {}km", latitude, longitude, radiusKm);
        
        List<ParcelLocation> locations = parcelLocationRepository
                .findByLocationNear(latitude, longitude, radiusKm * 1000); // Convert km to meters
        
        return locations.stream()
                .map(location -> {
                    TrackingEvent latest = trackingEventRepository
                            .findFirstByTrackingNumberOrderByTimestampDesc(location.getTrackingNumber())
                            .orElse(null);
                    if (latest != null) {
                        List<TrackingEvent> history = trackingEventRepository
                                .findByTrackingNumberOrderByTimestampDesc(location.getTrackingNumber());
                        return buildTrackingResponse(latest, history);
                    }
                    return null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }
    
    private void publishTrackingUpdateEvent(TrackingEvent event) {
        try {
            kafkaTemplate.send("tracking-updates", event.getTrackingNumber(), event);
            log.info("Published tracking update event for: {}", event.getTrackingNumber());
        } catch (Exception e) {
            log.error("Failed to publish tracking update event", e);
        }
    }
    
    private TrackingResponse buildTrackingResponse(TrackingEvent latestEvent, List<TrackingEvent> history) {
        // Generate Google Maps URLs for frontend
        String mapUrl = generateGoogleMapsUrl(latestEvent.getLatitude(), latestEvent.getLongitude());
        String staticMapUrl = generateStaticMapUrl(latestEvent.getLatitude(), latestEvent.getLongitude());
        
        return TrackingResponse.builder()
                .trackingNumber(latestEvent.getTrackingNumber())
                .status(latestEvent.getStatus())
                .description(latestEvent.getDescription())
                .location(latestEvent.getAddress())
                .latitude(latestEvent.getLatitude())
                .longitude(latestEvent.getLongitude())
                .address(latestEvent.getAddress())
                .city(latestEvent.getCity())
                .state(latestEvent.getState())
                .zipCode(latestEvent.getZipCode())
                .country(latestEvent.getCountry())
                .formattedAddress(latestEvent.getFormattedAddress())
                .timestamp(latestEvent.getTimestamp())
                .driverId(latestEvent.getDriverId())
                .vehicleId(latestEvent.getVehicleId())
                .notes(latestEvent.getNotes())
                .accuracy(latestEvent.getAccuracy())
                .placeId(latestEvent.getPlaceId())
                .mapUrl(mapUrl)
                .staticMapUrl(staticMapUrl)
                .estimatedDeliveryTime(calculateEstimatedDeliveryTime(latestEvent))
                .currentLocation(latestEvent.getFormattedAddress())
                .isDelivered("DELIVERED".equals(latestEvent.getStatus()))
                .deliveryStatus(latestEvent.getStatus())
                .history(history.stream()
                        .map(this::buildTrackingEventResponse)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private TrackingEventResponse buildTrackingEventResponse(TrackingEvent event) {
        return TrackingEventResponse.builder()
                .id(event.getId())
                .status(event.getStatus())
                .description(event.getDescription())
                .location(event.getAddress())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .address(event.getAddress())
                .city(event.getCity())
                .state(event.getState())
                .zipCode(event.getZipCode())
                .country(event.getCountry())
                .timestamp(event.getTimestamp())
                .driverId(event.getDriverId())
                .vehicleId(event.getVehicleId())
                .notes(event.getNotes())
                .placeId(event.getPlaceId())
                .formattedAddress(event.getFormattedAddress())
                .build();
    }
    
    private String extractCity(GeocodingResponse geocoding) {
        return geocoding.getAddressComponents().stream()
                .filter(component -> component.getTypes().contains("locality"))
                .map(GeocodingResponse.AddressComponent::getLongName)
                .findFirst()
                .orElse(null);
    }
    
    private String extractState(GeocodingResponse geocoding) {
        return geocoding.getAddressComponents().stream()
                .filter(component -> component.getTypes().contains("administrative_area_level_1"))
                .map(GeocodingResponse.AddressComponent::getLongName)
                .findFirst()
                .orElse(null);
    }
    
    private String extractZipCode(GeocodingResponse geocoding) {
        return geocoding.getAddressComponents().stream()
                .filter(component -> component.getTypes().contains("postal_code"))
                .map(GeocodingResponse.AddressComponent::getLongName)
                .findFirst()
                .orElse(null);
    }
    
    private String extractCountry(GeocodingResponse geocoding) {
        return geocoding.getAddressComponents().stream()
                .filter(component -> component.getTypes().contains("country"))
                .map(GeocodingResponse.AddressComponent::getLongName)
                .findFirst()
                .orElse(null);
    }
    
    // Google Maps URL generation methods for frontend
    private String generateGoogleMapsUrl(Double latitude, Double longitude) {
        return String.format("https://www.google.com/maps?q=%f,%f", latitude, longitude);
    }
    
    private String generateStaticMapUrl(Double latitude, Double longitude) {
        return String.format("https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=400x300&markers=color:red%%7C%f,%f&key=%s", 
                latitude, longitude, latitude, longitude, apiKey);
    }
    
    private Double calculateEstimatedDeliveryTime(TrackingEvent event) {
        // Simple estimation based on status
        switch (event.getStatus()) {
            case "PICKED_UP":
                return 120.0; // 2 hours
            case "IN_TRANSIT":
                return 60.0; // 1 hour
            case "OUT_FOR_DELIVERY":
                return 30.0; // 30 minutes
            case "DELIVERED":
                return 0.0;
            default:
                return 180.0; // 3 hours default
        }
    }
}

