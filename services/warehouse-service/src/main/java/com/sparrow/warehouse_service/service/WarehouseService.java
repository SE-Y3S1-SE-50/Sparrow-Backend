package com.sparrow.warehouse_service.service;

import com.sparrow.warehouse_service.dto.WarehouseCreateRequest;
import com.sparrow.warehouse_service.dto.WarehouseResponse;
import com.sparrow.warehouse_service.dto.WarehouseUpdateRequest;
import com.sparrow.warehouse_service.model.Warehouse;
import com.sparrow.warehouse_service.repo.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.sparrow.warehouse_service.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Arrays;



@Service
@RequiredArgsConstructor

public class WarehouseService {

    private final WarehouseRepository repo;

    public WarehouseResponse create(WarehouseCreateRequest req) {
        System.out.println("[WS] create capacity=" + req.getCapacity());

        if (repo.existsByNameAndCity(req.getName().trim(), req.getCity().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Warehouse Already Exists in This City");
        }

        Warehouse toSave = Warehouse.builder()
                .name(req.getName().trim())
                .address(req.getAddress().trim())
                .city(req.getCity().trim())
                .country(req.getCountry().trim())
                .capacity(req.getCapacity())
                .active(req.getActive() == null ? Boolean.TRUE : req.getActive())
                .build();

        Warehouse saved = repo.save(toSave);
        return toResponse(saved);
    }

    public WarehouseResponse get(String id) {
        Warehouse w = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));
        return toResponse(w);
    }

    public WarehouseResponse update(String id, WarehouseUpdateRequest req) {
        Warehouse w = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));

        boolean nameChanged = req.getName() != null && !req.getName().trim().equals(w.getName());
        boolean cityChanged = req.getCity() != null && !req.getCity().trim().equals(w.getCity());

        if(nameChanged || cityChanged) {
            String newName = req.getName() != null ? req.getName().trim() : w.getName();
            String newCity = req.getCity() != null ? req.getCity().trim() : w.getCity();

            if (repo.existsByNameAndCity(newName, newCity)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Warehouse already exist in this city");
            }
        }

        if (req.getName() != null)    w.setName(req.getName().trim());
        if (req.getAddress() != null) w.setAddress(req.getAddress().trim());
        if (req.getCity() != null)    w.setCity(req.getCity().trim());
        if (req.getCountry() != null)    w.setCountry(req.getCountry().trim());
        if (req.getCapacity() != null)    w.setCapacity(req.getCapacity());
        if (req.getActive() != null)    w.setActive(req.getActive());

        Warehouse saved = repo.save(w);
        return toResponse(saved);
    }

    public void delete(String id) {
        if(!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found");
        }
        repo.deleteById(id);
    }

    private static WarehouseResponse toResponse(Warehouse w) {
        return WarehouseResponse.builder()
                .id(w.getId())
                .name(w.getName())
                .address(w.getAddress())
                .city(w.getCity())
                .country(w.getCountry())
                .capacity(w.getCapacity())
                .active(w.getActive())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }

    public PagedResponse<WarehouseResponse> list(String city, String q, int page, int size, String sort) {
        // default sort by name asc
        Sort sortObj = Sort.by("name").ascending();
        if (sort != null && !sort.isBlank()) {
            // e.g. sort=name   or   sort=-createdAt   or   sort=city,-name
            sortObj = Sort.by(Arrays.stream(sort.split(","))
                    .map(s -> {
                        s = s.trim();
                        if (s.startsWith("-")) return Sort.Order.desc(s.substring(1));
                        if (s.startsWith("+")) return Sort.Order.asc(s.substring(1));
                        return Sort.Order.asc(s);
                    })
                    .toList());
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        String cityFilter = city == null ? "" : city;
        String nameFilter = q == null ? "" : q;

        Page<Warehouse> p = repo.findByCityIgnoreCaseContainingAndNameIgnoreCaseContaining(
                cityFilter, nameFilter, pageable);

        return PagedResponse.<WarehouseResponse>builder()
                .items(p.getContent().stream().map(WarehouseService::toResponse).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .last(p.isLast())
                .build();
    }

}