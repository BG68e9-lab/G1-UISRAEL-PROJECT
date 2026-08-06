package com.uisrael.drinkhouse.infraestructura.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

/**
 * Utilidad para convertir documentos PDF a imágenes.
 * Utiliza Apache PDFBox para renderizar páginas de PDF como imágenes JPEG.
 */
@Component
public class PdfToImageConverter {

    /**
     * Convierte un PDF (en base64) a una lista de imágenes (una por página).
     * Cada imagen es devuelta como base64 en formato JPEG.
     * 
     * @param pdfBase64 PDF codificado en base64
     * @param dpi resolución de renderizado (típicamente 150-300 DPI)
     * @return lista de imágenes en base64 (formato JPEG), una por cada página del PDF
     * @throws IOException si ocurre un error al procesar el PDF
     */
    public List<String> convertirPdfAImagenes(String pdfBase64, int dpi) throws IOException {
        byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
        
        List<String> imagenesBase64 = new ArrayList<>();
        
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                BufferedImage imagen = renderer.renderImageWithDPI(pageIndex, dpi);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imagen, "JPEG", baos);
                byte[] imageBytes = baos.toByteArray();
                String imagenBase64 = Base64.getEncoder().encodeToString(imageBytes);
                
                imagenesBase64.add(imagenBase64);
            }
        }
        
        return imagenesBase64;
    }
    
    /**
     * Convierte solo la primera página de un PDF a imagen JPEG.
     * Útil para facturas que típicamente son de una sola página.
     * 
     * @param pdfBase64 PDF codificado en base64
     * @param dpi resolución de renderizado (150-300 DPI recomendado para OCR)
     * @return imagen de la primera página en base64 (formato JPEG)
     * @throws IOException si ocurre un error al procesar el PDF
     */
    public String convertirPrimeraPaginaAImagen(String pdfBase64, int dpi) throws IOException {
        List<String> imagenes = convertirPdfAImagenes(pdfBase64, dpi);
        if (imagenes.isEmpty()) {
            throw new IOException("El PDF no contiene páginas");
        }
        return imagenes.get(0);
    }
    
    /**
     * Detecta si un string base64 representa un PDF.
     * Verifica el magic number del PDF (%PDF-).
     * 
     * @param base64Data datos en base64
     * @return true si es un PDF, false en caso contrario
     */
    public boolean esPdf(String base64Data) {
        try {
            byte[] datos = Base64.getDecoder().decode(base64Data);
            if (datos.length < 5) {
                return false;
            }
            String header = new String(datos, 0, 5);
            return header.equals("%PDF-");
        } catch (Exception e) {
            return false;
        }
    }
}
