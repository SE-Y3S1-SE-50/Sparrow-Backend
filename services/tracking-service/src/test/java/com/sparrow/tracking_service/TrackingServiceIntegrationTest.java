package com.sparrow.tracking_service;

import com.sparrow.tracking_service.dto.TrackingUpdateRequest;
import com.sparrow.tracking_service.dto.TrackingResponse;
import com.sparrow.tracking_service.service.TrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TrackingServiceIntegrationTest {

    @Autowired
    private TrackingService trackingService;

    @Test
    public void testTrackingUpdateWithGoogleMapsIntegration() {
        // Test with real coordinates (New York City)
        TrackingUpdateRequest request = TrackingUpdateRequest.builder()
                .trackingNumber("TEST123456")
                .status("IN_TRANSIT")
                .description("Package in transit")
                .latitude(40.7128)
                .longitude(-74.0060)
                .driverId("DRIVER001")
                .vehicleId("VEHICLE001")
                .notes("Test tracking update")
                .accuracy(10.0)
                .build();

        TrackingResponse response = trackingService.updateTracking(request);

        assertNotNull(response);
        assertEquals("TEST123456", response.getTrackingNumber());
        assertEquals("IN_TRANSIT", response.getStatus());
        assertEquals(40.7128, response.getLatitude());
        assertEquals(-74.0060, response.getLongitude());
        assertNotNull(response.getAddress());
        assertNotNull(response.getMapUrl());
        assertNotNull(response.getStaticMapUrl());
        assertNotNull(response.getPlaceId());
        assertTrue(response.getEstimatedDeliveryTime() > 0);
        assertFalse(response.getIsDelivered());
    }
}
