package com.sparrow.tracking_service.repository;

import com.sparrow.tracking_service.model.TrackingEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingEventRepository extends MongoRepository<TrackingEvent, String> {
    
    List<TrackingEvent> findByTrackingNumberOrderByTimestampDesc(String trackingNumber);
    
    List<TrackingEvent> findByParcelIdOrderByTimestampDesc(String parcelId);
    
    Page<TrackingEvent> findByTrackingNumberOrderByTimestampDesc(String trackingNumber, Pageable pageable);
    
    Optional<TrackingEvent> findFirstByTrackingNumberOrderByTimestampDesc(String trackingNumber);
    
    @Query("{'trackingNumber': ?0, 'timestamp': {$gte: ?1}}")
    List<TrackingEvent> findByTrackingNumberAndTimestampAfter(String trackingNumber, Instant timestamp);
    
    @Query("{'status': ?0, 'timestamp': {$gte: ?1}}")
    List<TrackingEvent> findByStatusAndTimestampAfter(String status, Instant timestamp);
    
    @Query("{'driverId': ?0, 'timestamp': {$gte: ?1}}")
    List<TrackingEvent> findByDriverIdAndTimestampAfter(String driverId, Instant timestamp);
    
    @Query("{'latitude': {$gte: ?0, $lte: ?2}, 'longitude': {$gte: ?1, $lte: ?3}}")
    List<TrackingEvent> findByLocationWithin(Double minLat, Double minLng, Double maxLat, Double maxLng);
}

