package com.sparrow.parcel_service.controller;

import com.sparrow.parcel_service.dto.ImageBase64Response;
import java.util.Base64;
import com.sparrow.parcel_service.service.QrBarcodeService;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.sparrow.parcel_service.dto.PagedResponse;
import com.sparrow.parcel_service.dto.ParcelCreateRequest;
import com.sparrow.parcel_service.dto.ParcelResponse;
import com.sparrow.parcel_service.dto.ParcelUpdateRequest;
import com.sparrow.parcel_service.service.ParcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService service;
    private final QrBarcodeService qrBarcodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_STAFF')")
    public ParcelResponse create(@Valid @RequestBody ParcelCreateRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ParcelResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/track/{trackingNumber}")
    @PreAuthorize("isAuthenticated()") // FIXED
    public ParcelResponse getByTracking(@PathVariable String trackingNumber) {
        return service.getByTracking(trackingNumber);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public PagedResponse<ParcelResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        return service.list(origin, destination, q, page, size, sort);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_STAFF')")
    public ParcelResponse updatePut(@PathVariable String id,
                                    @Valid @RequestBody ParcelUpdateRequest req) {
        return service.update(id, req);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_STAFF')")
    public ParcelResponse updatePatch(@PathVariable String id,
                                      @Valid @RequestBody ParcelUpdateRequest req) {
        return service.update(id, req);
    }

    // Delete (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]>qrPng(
            @PathVariable String id,
            @RequestParam(defaultValue = "320") int size
    ) {
        int safe = Math.max(128, Math.min(size, 1024));

        var parcel = service.getById(id);

        byte[] png = qrBarcodeService.generateQrPng(parcel.getTrackingNumber(), safe);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"parcel-" + parcel.getTrackingNumber() + "-qr.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    @GetMapping(value = "/{id}/barcode.png", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> barcodePng(
            @PathVariable String id,
            @RequestParam(defaultValue = "600") int width,
            @RequestParam(defaultValue = "180") int height
    ) {

        int w = Math.max(300, Math.min(width, 1600));
        int h = Math.max(100, Math.min(height, 400));


        var parcel = service.getById(id);


        byte[] png = qrBarcodeService.generateCode128Png(parcel.getTrackingNumber(), w, h);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"parcel-" + parcel.getTrackingNumber() + "-code128.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    @GetMapping("/{id}/qr")
    @PreAuthorize("isAuthenticated()")
    public ImageBase64Response qrBase64(
            @PathVariable String id,
            @RequestParam(defaultValue = "320") int size
    ) {

        int safe = Math.max(128, Math.min(size, 1024));

        var parcel = service.getById(id);

        byte[] png = qrBarcodeService.generateQrPng(parcel.getTrackingNumber(), safe);

        String b64 = Base64.getEncoder().encodeToString(png);

        return new ImageBase64Response(
                parcel.getTrackingNumber(),
                "image/png",
                b64,
                png.length
        );
    }
}
