package com.sparrow.consolidation_service.repository;

import com.sparrow.consolidation_service.model.ParcelConsolidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelConsolidationRepository extends MongoRepository<ParcelConsolidation, String> {
    
    Optional<ParcelConsolidation> findByParcelId(String parcelId);
    
    Optional<ParcelConsolidation> findByTrackingNumber(String trackingNumber);
    
    List<ParcelConsolidation> findByConsolidationGroupId(String consolidationGroupId);
    
    List<ParcelConsolidation> findByStatus(ParcelConsolidation.ConsolidationStatus status);
    
    List<ParcelConsolidation> findByDestinationZip(String destinationZip);
    
    List<ParcelConsolidation> findByDestinationCity(String destinationCity);
    
    List<ParcelConsolidation> findByDestinationState(String destinationState);
    
    @Query("{'destinationZip': ?0, 'status': ?1}")
    List<ParcelConsolidation> findByDestinationZipAndStatus(String destinationZip, ParcelConsolidation.ConsolidationStatus status);
    
    @Query("{'destinationCity': ?0, 'status': ?1}")
    List<ParcelConsolidation> findByDestinationCityAndStatus(String destinationCity, ParcelConsolidation.ConsolidationStatus status);
    
    @Query("{'destinationState': ?0, 'status': ?1}")
    List<ParcelConsolidation> findByDestinationStateAndStatus(String destinationState, ParcelConsolidation.ConsolidationStatus status);
    
    @Query("{'createdAt': {$gte: ?0}, 'status': ?1}")
    List<ParcelConsolidation> findByCreatedAtAfterAndStatus(Instant createdAt, ParcelConsolidation.ConsolidationStatus status);
    
    @Query("{'destinationZip': {$in: ?0}, 'status': ?1}")
    List<ParcelConsolidation> findByDestinationZipInAndStatus(List<String> destinationZips, ParcelConsolidation.ConsolidationStatus status);
    
    @Query("{'destinationCity': {$in: ?0}, 'status': ?1}")
    List<ParcelConsolidation> findByDestinationCityInAndStatus(List<String> destinationCities, ParcelConsolidation.ConsolidationStatus status);
    
    @Query("{'destinationState': {$in: ?0}, 'status': ?1}")
    List<ParcelConsolidation> findByDestinationStateInAndStatus(List<String> destinationStates, ParcelConsolidation.ConsolidationStatus status);
    
    Page<ParcelConsolidation> findByStatus(ParcelConsolidation.ConsolidationStatus status, Pageable pageable);
}

