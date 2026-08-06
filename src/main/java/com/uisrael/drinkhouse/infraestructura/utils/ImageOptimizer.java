package com.uisrael.drinkhouse.infraestructura.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.springframework.stereotype.Component;

import net.coobird.thumbnailator.Thumbnails;

/**
 * Utilidad para optimizar imágenes antes de enviarlas a Claude Vision API.
 * 
 * Aplica las siguientes optimizaciones para reducir el costo de tokens:
 * 1. Redimensiona la imagen a un máximo de 1024px (lado más largo)
 * 2. Convierte a formato JPEG con compresión de calidad 85%
 * 3. Codifica el resultado optimizado en Base64
 * 
 * Anthropic recomienda no superar 1568px en el lado más largo (~1.15 megapíxeles).
 * La fórmula aproximada es: tokens_imagen = (ancho × alto) / 750
 * 
 * Con estas optimizaciones, se logra una reducción de 80-90% en tokens de imagen
 * comparado con fotos de cámara típicas (3000-4000px).
 */
@Component
public class ImageOptimizer {

    /**
     * Dimensión máxima recomendada (1024px es suficiente para leer etiquetas de productos).
     * Mantiene el aspect ratio, no excede este límite en ningún lado.
     */
    private static final int MAX_DIMENSION = 1024;

    /**
     * Calidad de compresión JPEG (85% es el punto óptimo entre tamaño y legibilidad).
     * Rango: 0.0f (mínima calidad) a 1.0f (máxima calidad).
     */
    private static final float JPEG_QUALITY = 0.85f;

    /**
     * Optimiza una imagen codificada en Base64 y retorna la versión optimizada también en Base64.
     * 
     * Pasos:
     * 1. Decodifica el Base64 a bytes
     * 2. Redimensiona manteniendo aspect ratio
     * 3. Comprime como JPEG calidad 85%
     * 4. Recodifica a Base64
     * 
     * @param imagenBase64Original imagen original codificada en Base64 (puede ser PNG, JPEG, etc.)
     * @return imagen optimizada codificada en Base64 (siempre JPEG)
     * @throws Exception si ocurre un error durante el procesamiento de la imagen
     */
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

    /**
     * Optimiza una imagen que ya está en formato de bytes y retorna Base64.
     * 
     * Útil cuando se recibe la imagen directamente como MultipartFile o InputStream.
     * 
     * @param bytesOriginales bytes de la imagen original
     * @return imagen optimizada codificada en Base64 (siempre JPEG)
     * @throws Exception si ocurre un error durante el procesamiento de la imagen
     */
    public String optimizarImagenDesdeBytes(byte[] bytesOriginales) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(bytesOriginales))
                .size(MAX_DIMENSION, MAX_DIMENSION)
                .outputFormat("jpg")
                .outputQuality(JPEG_QUALITY)
                .toOutputStream(outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * Verifica si una imagen necesita optimización basándose en su tamaño en bytes.
     * 
     * Una imagen de 1024x1024 JPEG comprimida pesa típicamente ~200-400KB.
     * Si la imagen es mayor a 500KB, probablemente se beneficie de la optimización.
     * 
     * @param imagenBase64 imagen codificada en Base64
     * @return true si la imagen es mayor a 500KB y debería optimizarse
     */
    public boolean necesitaOptimizacion(String imagenBase64) {
        final int UMBRAL_BYTES_BASE64 = 667_000;
        return imagenBase64.length() > UMBRAL_BYTES_BASE64;
    }
}
