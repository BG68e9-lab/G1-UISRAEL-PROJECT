package com.uisrael.drinkhouse.infraestructura.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.springframework.stereotype.Component;

import net.coobird.thumbnailator.Thumbnails;

@Component
public class ImageOptimizer {

private static final int MAX_DIMENSION = 1024;

private static final float JPEG_QUALITY = 0.85f;

public String optimizarImagen(String imagenBase64Original) throws Exception {
        byte[] bytesOriginales = Base64.getDecoder().decode(imagenBase64Original);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(bytesOriginales))
                .size(MAX_DIMENSION, MAX_DIMENSION)
                .outputFormat("jpg")
                .outputQuality(JPEG_QUALITY)
                .toOutputStream(outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

public String optimizarImagenDesdeBytes(byte[] bytesOriginales) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(bytesOriginales))
                .size(MAX_DIMENSION, MAX_DIMENSION)
                .outputFormat("jpg")
                .outputQuality(JPEG_QUALITY)
                .toOutputStream(outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

public boolean necesitaOptimizacion(String imagenBase64) {
        final int UMBRAL_BYTES_BASE64 = 667_000;
        return imagenBase64.length() > UMBRAL_BYTES_BASE64;
    }
}
