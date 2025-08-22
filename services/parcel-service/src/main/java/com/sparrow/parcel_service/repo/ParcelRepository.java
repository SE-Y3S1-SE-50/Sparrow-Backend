package com.sparrow.parcel_service.repo;

import com.sparrow.parcel_service.model.Parcel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

public interface ParcelRepository extends MongoRepository<Parcel, String> {
    Optional<Parcel> findByTrackingNumber(String trackingNumber);
    boolean existsByTrackingNumber(String trackingNumber);

    Page<Parcel> findByOriginIgnoreCaseContainingAndDestinationIgnoreCaseContainingAndTrackingNumberIgnoreCaseContaining(
            String origin, String destination, String trackingNumber, Pageable pageable
    );
}


