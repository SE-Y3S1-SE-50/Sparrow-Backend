package com.sparrow.warehouse_service.controller;

import com.sparrow.warehouse_service.dto.WarehouseCreateRequest;
import com.sparrow.warehouse_service.dto.WarehouseResponse;
import com.sparrow.warehouse_service.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.sparrow.warehouse_service.dto.PagedResponse;
import com.sparrow.warehouse_service.dto.WarehouseUpdateRequest;


import java.net.URI;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor

public class WarehouseController {
    private final WarehouseService service;

    @PostMapping
    public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody WarehouseCreateRequest req,
                                                    UriComponentsBuilder uri) {
        WarehouseResponse res = service.create(req);
        URI location = uri.path("/api/warehouses/{id}").buildAndExpand(res.getId()).toUri();
        return ResponseEntity.created(location).body(res);
    }


    @GetMapping
    public ResponseEntity<PagedResponse<WarehouseResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String city,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "sort", required = false) String sort) {

        return ResponseEntity.ok(service.list(city, q, page, size, sort));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updatePut(
            @PathVariable String id,
            @Valid @RequestBody WarehouseUpdateRequest req) {
        return ResponseEntity.ok(service.update(id,req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updatePatch(
            @PathVariable String id,
            @Valid @RequestBody WarehouseUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);                 // 404 if not found (already handled in service)
        return ResponseEntity.noContent().build();   // 204 No Content
    }


}
