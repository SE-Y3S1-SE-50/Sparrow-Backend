package com.sparrow.warehouse_service.web;

import com.sparrow.warehouse_service.model.Warehouse;
import com.sparrow.warehouse_service.repo.WarehouseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseRepository repo;

    @PostMapping
    public ResponseEntity<Warehouse> create(@Valid @RequestBody Warehouse w) {
        Warehouse saved = repo.save(w);
        return ResponseEntity.created(URI.create("/warehouses/" + saved.getId())).body(saved);
    }

    @GetMapping
    public Iterable<Warehouse> list(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> update(@PathVariable String id, @Valid @RequestBody Warehouse w) {
        return repo.findById(id).map(ex -> {
            w.setId(id);
            return ResponseEntity.ok(repo.save(w));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
