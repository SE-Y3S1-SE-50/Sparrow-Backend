package com.sparrow.tracking_service.repository;

import com.sparrow.tracking_service.model.ParcelLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelLocationRepository extends MongoRepository<ParcelLocation, String> {
    
    List<ParcelLocation> findByTrackingNumberOrderByTimestampDesc(String trackingNumber);
    
    Optional<ParcelLocation> findFirstByTrackingNumberOrderByTimestampDesc(String trackingNumber);
    
    @Query("{'trackingNumber': ?0, 'timestamp': {$gte: ?1}}")
    List<ParcelLocation> findByTrackingNumberAndTimestampAfter(String trackingNumber, Instant timestamp);
    
    @Query("{'status': ?0, 'timestamp': {$gte: ?1}}")
    List<ParcelLocation> findByStatusAndTimestampAfter(String status, Instant timestamp);
    
    @Query("{'driverId': ?0, 'timestamp': {$gte: ?1}}")
    List<ParcelLocation> findByDriverIdAndTimestampAfter(String driverId, Instant timestamp);
    
    // Geospatial queries
    List<ParcelLocation> findByCoordinatesNear(Point point, Distance distance);
    
    @Query("{'coordinates': {$near: {$geometry: {type: 'Point', coordinates: [?1, ?0]}, $maxDistance: ?2}}}")
    List<ParcelLocation> findByLocationNear(Double latitude, Double longitude, Double maxDistance);
    
    @Query("{'zipCode': ?0, 'timestamp': {$gte: ?1}}")
    List<ParcelLocation> findByZipCodeAndTimestampAfter(String zipCode, Instant timestamp);
    
    @Query("{'city': ?0, 'timestamp': {$gte: ?1}}")
    List<ParcelLocation> findByCityAndTimestampAfter(String city, Instant timestamp);
}

