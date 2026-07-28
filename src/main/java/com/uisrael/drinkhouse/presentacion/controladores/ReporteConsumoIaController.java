package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.infraestructura.servicios.ReporteConsumoIaService;
import com.uisrael.drinkhouse.presentacion.dto.response.ReporteConsumoIaMensualDto;

/**
 * Controlador REST para reportes de consumo de IA.
 * Base URL: /api/v1/reportes/consumo-ia
 */
@RestController
@RequestMapping("/api/v1/reportes/consumo-ia")
public class ReporteConsumoIaController {

    private final ReporteConsumoIaService reporteService;

    public ReporteConsumoIaController(ReporteConsumoIaService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * GET /api/v1/reportes/consumo-ia/mes-actual
     * Obtiene el reporte de consumo del mes en curso.
     *
     * @return lista de reportes por negocio del mes actual
     */
    @GetMapping("/mes-actual")
    public ResponseEntity<List<ReporteConsumoIaMensualDto>> obtenerReporteMesActual() {
        List<ReporteConsumoIaMensualDto> reporte = reporteService.obtenerReporteMesActual();
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/v1/reportes/consumo-ia/mes-anterior
     * Obtiene el reporte de consumo del mes pasado.
     *
     * @return lista de reportes por negocio del mes anterior
     */
    @GetMapping("/mes-anterior")
    public ResponseEntity<List<ReporteConsumoIaMensualDto>> obtenerReporteMesAnterior() {
        List<ReporteConsumoIaMensualDto> reporte = reporteService.obtenerReporteMesAnterior();
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/v1/reportes/consumo-ia/periodo?fecha=YYYY-MM-DD
     * Obtiene el reporte de consumo para un período específico.
     *
     * @param fecha fecha del período (se usa el primer día del mes)
     * @return lista de reportes por negocio del período especificado
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<ReporteConsumoIaMensualDto>> obtenerReportePorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        LocalDate periodo = fecha.withDayOfMonth(1);
        List<ReporteConsumoIaMensualDto> reporte = reporteService.generarReporte(periodo);
        return ResponseEntity.ok(reporte);
    }
}
