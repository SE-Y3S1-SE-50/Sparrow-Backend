package com.sparrow.parcel_service.dto;     // put it with other DTOs

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageBase64Response {
    private String trackingNumber;
    private String mimeType;
    private String base64;
    private int    bytes;
}
