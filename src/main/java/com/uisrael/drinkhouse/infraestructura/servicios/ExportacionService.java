package com.uisrael.drinkhouse.infraestructura.servicios;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;

/**
 * Servicio para exportar datos a diferentes formatos (PDF, Excel).
 */
@Service
public class ExportacionService {

    private static final Logger logger = LoggerFactory.getLogger(ExportacionService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporta una lista de movimientos de inventario a formato Excel (XLSX).
     *
     * @param movimientos lista de movimientos a exportar
     * @return array de bytes del archivo Excel generado
     */
    public byte[] exportarMovimientosAExcel(List<MovimientoInventarioResponseDto> movimientos) {
        logger.info("Exportando {} movimientos a Excel", movimientos.size());

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Movimientos Inventario");

            // Crear estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            String[] columnas = { "Código", "Tipo", "Producto", "Lote", "Cantidad", "Precio Unitario",
                    "Fecha Movimiento", "Estado Respaldo", "Estado Caducidad" };

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (MovimientoInventarioResponseDto mov : movimientos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(mov.getCodigoMovimiento());
                row.createCell(1).setCellValue(mov.getTipoMovimiento());
                row.createCell(2).setCellValue("N/A"); // productoNombre no está en DTO
                row.createCell(3).setCellValue("N/A"); // loteCodigo no está en DTO
                row.createCell(4).setCellValue(mov.getCantidad() != null ? mov.getCantidad().doubleValue() : 0.0);
                row.createCell(5).setCellValue(mov.getPrecioUnitario() != null ? mov.getPrecioUnitario().doubleValue() : 0.0);
                row.createCell(6).setCellValue(mov.getCreadoEn() != null ? mov.getCreadoEn().format(DATE_FORMATTER) : "N/A");
                row.createCell(7).setCellValue("N/A"); // estadoRespaldo no está en DTO
                row.createCell(8).setCellValue("N/A"); // estadoCaducidad no está en DTO
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            logger.info("Excel generado exitosamente");
            return out.toByteArray();

        } catch (Exception e) {
            logger.error("Error al exportar movimientos a Excel", e);
            throw new RuntimeException("Error al generar archivo Excel", e);
        }
    }

    /**
     * Exporta una lista de movimientos de inventario a formato PDF.
     *
     * @param movimientos lista de movimientos a exportar
     * @return array de bytes del archivo PDF generado
     */
    public byte[] exportarMovimientosAPdf(List<MovimientoInventarioResponseDto> movimientos) {
        logger.info("Exportando {} movimientos a PDF", movimientos.size());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            Paragraph titulo = new Paragraph("Historial de Movimientos de Inventario")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Crear tabla con 9 columnas
            float[] columnWidths = { 2f, 1.5f, 2f, 2f, 1f, 1.5f, 2f, 1.5f, 1.5f };
            Table table = new Table(columnWidths);
            table.setWidth(550);

            // Encabezados
            String[] headers = { "Código", "Tipo", "Producto", "Lote", "Cant.", "P. Unit.",
                    "Fecha", "Respaldo", "Caducidad" };

            for (String header : headers) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(header))
                        .setBold());
            }

            // Datos
            for (MovimientoInventarioResponseDto mov : movimientos) {
                table.addCell(mov.getCodigoMovimiento());
                table.addCell(mov.getTipoMovimiento());
                table.addCell("N/A"); // productoNombre no está en DTO
                table.addCell("N/A"); // loteCodigo no está en DTO
                table.addCell(mov.getCantidad() != null ? String.valueOf(mov.getCantidad()) : "0");
                table.addCell(mov.getPrecioUnitario() != null ? String.format("%.2f", mov.getPrecioUnitario()) : "0.00");
                table.addCell(mov.getCreadoEn() != null ? mov.getCreadoEn().format(DATE_FORMATTER) : "N/A");
                table.addCell("N/A"); // estadoRespaldo no está en DTO
                table.addCell("N/A"); // estadoCaducidad no está en DTO
            }

            document.add(table);

            // Pie de página
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total de movimientos: " + movimientos.size())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setItalic());

            document.close();
            logger.info("PDF generado exitosamente");
            return out.toByteArray();

        } catch (Exception e) {
            logger.error("Error al exportar movimientos a PDF", e);
            throw new RuntimeException("Error al generar archivo PDF", e);
        }
    }
}
