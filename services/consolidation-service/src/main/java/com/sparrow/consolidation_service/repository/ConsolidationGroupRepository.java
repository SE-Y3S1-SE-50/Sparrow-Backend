package com.sparrow.consolidation_service.repository;

import com.sparrow.consolidation_service.model.ConsolidationGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsolidationGroupRepository extends MongoRepository<ConsolidationGroup, String> {
    
    Optional<ConsolidationGroup> findByGroupId(String groupId);
    
    List<ConsolidationGroup> findByStatus(ConsolidationGroup.ConsolidationStatus status);
    
    List<ConsolidationGroup> findByDestinationZip(String destinationZip);
    
    List<ConsolidationGroup> findByDestinationCity(String destinationCity);
    
    List<ConsolidationGroup> findByDestinationState(String destinationState);
    
    List<ConsolidationGroup> findByType(ConsolidationGroup.ConsolidationType type);
    
    @Query("{'destinationZip': ?0, 'status': ?1}")
    List<ConsolidationGroup> findByDestinationZipAndStatus(String destinationZip, ConsolidationGroup.ConsolidationStatus status);
    
    @Query("{'destinationCity': ?0, 'status': ?1}")
    List<ConsolidationGroup> findByDestinationCityAndStatus(String destinationCity, ConsolidationGroup.ConsolidationStatus status);
    
    @Query("{'assignedDriver': ?0, 'status': ?1}")
    List<ConsolidationGroup> findByAssignedDriverAndStatus(String assignedDriver, ConsolidationGroup.ConsolidationStatus status);
    
    @Query("{'warehouseId': ?0, 'status': ?1}")
    List<ConsolidationGroup> findByWarehouseIdAndStatus(String warehouseId, ConsolidationGroup.ConsolidationStatus status);
    
    @Query("{'createdAt': {$gte: ?0}, 'status': ?1}")
    List<ConsolidationGroup> findByCreatedAtAfterAndStatus(Instant createdAt, ConsolidationGroup.ConsolidationStatus status);
    
    @Query("{'scheduledPickup': {$gte: ?0, $lte: ?1}}")
    List<ConsolidationGroup> findByScheduledPickupBetween(Instant start, Instant end);
    
    @Query("{'scheduledDelivery': {$gte: ?0, $lte: ?1}}")
    List<ConsolidationGroup> findByScheduledDeliveryBetween(Instant start, Instant end);
    
    Page<ConsolidationGroup> findByStatus(ConsolidationGroup.ConsolidationStatus status, Pageable pageable);
}

