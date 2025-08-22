package com.sparrow.parcel_service.service;

import com.sparrow.parcel_service.dto.ParcelCreateRequest;
import com.sparrow.parcel_service.dto.ParcelResponse;
import com.sparrow.parcel_service.dto.ParcelUpdateRequest;
import com.sparrow.parcel_service.model.Parcel;
import com.sparrow.parcel_service.model.ParcelStatus;
import com.sparrow.parcel_service.repo.ParcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sparrow.parcel_service.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;




import java.time.Instant;
import java.util.Random;


@Service
@RequiredArgsConstructor

public class ParcelService {
    private final ParcelRepository repo;
    private final Random rnd = new Random();

    public ParcelResponse create(ParcelCreateRequest req) {
        String tn = (req.getTrackingNumber() == null || req.getTrackingNumber().isBlank())
                ? generateTrackingNumber()
                : req.getTrackingNumber();

        if (repo.existsByTrackingNumber(tn)) {
            throw new IllegalArgumentException("Tracking number already exists");
        }

        Parcel p = Parcel.builder()
                .trackingNumber(tn)
                .senderName(req.getSenderName())
                .receiverName(req.getReceiverName())
                .origin(req.getOrigin())
                .destination(req.getDestination())
                .weightKg(req.getWeightKg())
                .dimensions(req.getDimensions())
                .status(ParcelStatus.CREATED)
                .build();

        return toResponse(repo.save(p));
    }

    public ParcelResponse getById(String id) {
        Parcel p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Parcel not found"));
        return toResponse(p);
    }

    public ParcelResponse getByTracking(String trackingNumber) {
        Parcel p = repo.findByTrackingNumber(trackingNumber).orElseThrow(() -> new IllegalArgumentException("Parcel not found"));
        return toResponse(p);
    }

    private String generateTrackingNumber() {
        long ts = Instant.now().toEpochMilli();
        int suffix = 100000 + rnd.nextInt(900000);
        return "SPRW - " + ts + "-" + suffix;
    }

    private ParcelResponse toResponse(Parcel p) {
        return ParcelResponse.builder()
                .id(p.getId())
                .trackingNumber(p.getTrackingNumber())
                .senderName(p.getSenderName())
                .receiverName(p.getReceiverName())
                .origin(p.getOrigin())
                .destination(p.getDestination())
                .weightKg(p.getWeightKg())
                .dimensions(p.getDimensions())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public PagedResponse<ParcelResponse> list(String origin, String destination,String q,
                                              int page, int size, String sort){
        Sort sortObj = Sort.by(Sort.Order.desc("createdAt"));
        if (sort != null && !sort.isBlank()) {
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

        String o = origin == null ? "" : origin;
        String d = destination == null ? "" : destination;
        String tn = q == null ? "" : q;

        Page<Parcel> p = repo.findByOriginIgnoreCaseContainingAndDestinationIgnoreCaseContainingAndTrackingNumberIgnoreCaseContaining(o, d, tn, pageable);

        return PagedResponse.<ParcelResponse>builder()
                .items(p.getContent().stream().map(this::toResponse).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .last(p.isLast())
                .build();
    }

    public ParcelResponse update(String id, ParcelUpdateRequest req) {
        var p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"));
        if (req.getSenderName() != null)     p.setSenderName(req.getSenderName().trim());
        if (req.getReceiverName() != null)   p.setReceiverName(req.getReceiverName().trim());
        if (req.getOrigin() != null)         p.setOrigin(req.getOrigin().trim());
        if (req.getDestination() != null)    p.setDestination(req.getDestination().trim());
        if (req.getWeightKg() != null)       p.setWeightKg(req.getWeightKg());
        if (req.getDimensions() != null)     p.setDimensions(req.getDimensions().trim());
        if (req.getStatus() != null)         p.setStatus(req.getStatus());
        return toResponse(repo.save(p));
    }

    public void delete(String id) {
        if(!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Parcel not found");
        }
        repo.deleteById(id);
    }
}