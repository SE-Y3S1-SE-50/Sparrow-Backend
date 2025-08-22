package com.sparrow.parcel_service.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrBarcodeService {

    public byte[] generateQrPng(String text, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();

            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());

            hints.put(EncodeHintType.MARGIN, 1);

            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.QR_CODE,size, size, hints);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(matrix, "PNG", out);

                return out.toByteArray();
            }
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Failed to generate QR PNG", e);
        }
    }

    public byte[] generateCode128Png(String text, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();

            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.CODE_128,width, height, hints);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(matrix, "PNG", out);

                return out.toByteArray();
            }
        } catch (WriterException | IOException e)  {
            throw new IllegalStateException("Failed to generate Code128 PNG", e);
        }
    }
}