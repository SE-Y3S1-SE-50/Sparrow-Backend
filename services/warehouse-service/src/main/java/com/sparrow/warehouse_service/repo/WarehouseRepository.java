package com.sparrow.warehouse_service.repo;

import com.sparrow.warehouse_service.model.Warehouse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    boolean existsByNameAndCity(String name, String city);
    Optional<Warehouse> findByNameAndCity(String name, String city);
    Page<Warehouse> findByCityIgnoreCaseContainingAndNameIgnoreCaseContaining(
            String city, String name, Pageable pageable);
}
