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

@RestController
@RequestMapping("/api/v1/reportes/consumo-ia")
public class ReporteConsumoIaController {

    private final ReporteConsumoIaService reporteService;

    public ReporteConsumoIaController(ReporteConsumoIaService reporteService) {
        this.reporteService = reporteService;
    }

@GetMapping("/mes-actual")
    public ResponseEntity<List<ReporteConsumoIaMensualDto>> obtenerReporteMesActual() {
        List<ReporteConsumoIaMensualDto> reporte = reporteService.obtenerReporteMesActual();
        return ResponseEntity.ok(reporte);
    }

@GetMapping("/mes-anterior")
    public ResponseEntity<List<ReporteConsumoIaMensualDto>> obtenerReporteMesAnterior() {
        List<ReporteConsumoIaMensualDto> reporte = reporteService.obtenerReporteMesAnterior();
        return ResponseEntity.ok(reporte);
    }

@GetMapping("/periodo")
    public ResponseEntity<List<ReporteConsumoIaMensualDto>> obtenerReportePorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        LocalDate periodo = fecha.withDayOfMonth(1);
        List<ReporteConsumoIaMensualDto> reporte = reporteService.generarReporte(periodo);
        return ResponseEntity.ok(reporte);
    }
}
