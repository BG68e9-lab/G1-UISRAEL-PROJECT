package com.uisrael.drinkhouse.infraestructura.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ConsumoIaMensualEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.IConsumoIaMensualJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IIdentificacionIaJpaRepositorio;
import com.uisrael.drinkhouse.presentacion.dto.response.ReporteConsumoIaMensualDto;

@Service
public class ReporteConsumoIaService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteConsumoIaService.class);

    private final IConsumoIaMensualJpaRepositorio consumoRepositorio;
    private final IIdentificacionIaJpaRepositorio identificacionRepositorio;

    public ReporteConsumoIaService(
            IConsumoIaMensualJpaRepositorio consumoRepositorio,
            IIdentificacionIaJpaRepositorio identificacionRepositorio) {
        this.consumoRepositorio = consumoRepositorio;
        this.identificacionRepositorio = identificacionRepositorio;
    }

public List<ReporteConsumoIaMensualDto> generarReporte(LocalDate periodo) {
        logger.info("Generando reporte de consumo IA para período: {}", periodo);

        List<ConsumoIaMensualEntity> consumos = consumoRepositorio.findAll();

        return consumos.stream()
                .filter(c -> c.getPeriodo().equals(periodo))
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

public List<ReporteConsumoIaMensualDto> obtenerReporteMesActual() {
        LocalDate periodoActual = LocalDate.now().withDayOfMonth(1);
        return generarReporte(periodoActual);
    }

public List<ReporteConsumoIaMensualDto> obtenerReporteMesAnterior() {
        LocalDate periodoAnterior = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        return generarReporte(periodoAnterior);
    }

@Scheduled(cron = "0 0 8 1 * ?") // Día 1 de cada mes a las 8:00 AM
    public void generarReporteMensualAutomatico() {
        logger.info("Ejecutando generación automática de reporte mensual de IA");

        try {
            List<ReporteConsumoIaMensualDto> reporte = obtenerReporteMesAnterior();

            if (reporte.isEmpty()) {
                logger.info("No hay datos de consumo para reportar del mes anterior");
                return;
            }

            logger.info("Reporte generado exitosamente. Total de negocios: {}", reporte.size());
            
            int totalIdentificaciones = reporte.stream()
                    .mapToInt(ReporteConsumoIaMensualDto::getCantidadIdentificaciones)
                    .sum();
            int totalTokens = reporte.stream()
                    .mapToInt(ReporteConsumoIaMensualDto::getTokensConsumidos)
                    .sum();

            logger.info("Totales del período: {} identificaciones, {} tokens consumidos",
                    totalIdentificaciones, totalTokens);

        } catch (Exception e) {
            logger.error("Error al generar reporte mensual automático", e);
        }
    }

private ReporteConsumoIaMensualDto convertirADto(ConsumoIaMensualEntity entity) {
        Integer totalTokens = (entity.getTotalTokensInput() != null ? entity.getTotalTokensInput().intValue() : 0)
                + (entity.getTotalTokensOutput() != null ? entity.getTotalTokensOutput().intValue() : 0);
        
        String estadoCuota = determinarEstadoCuota(totalTokens);

        int cantidadIdentificaciones = identificacionRepositorio
                .contarPorNegocioYPeriodo(entity.getNegocio().getNegocioId(), entity.getPeriodo());

        return ReporteConsumoIaMensualDto.builder()
                .consumoIaId(entity.getConsumoIaId())
                .negocioId(entity.getNegocio().getNegocioId())
                .negocioNombre(entity.getNegocio().getNombre())
                .periodo(entity.getPeriodo())
                .cantidadIdentificaciones(cantidadIdentificaciones)
                .tokensConsumidos(totalTokens)
                .estadoCuota(estadoCuota)
                .build();
    }

private String determinarEstadoCuota(Integer tokensConsumidos) {
        if (tokensConsumidos == null || tokensConsumidos < 8000) {
            return "NORMAL";
        } else if (tokensConsumidos < 10000) {
            return "ADVERTENCIA";
        } else {
            return "LIMITE_ALCANZADO";
        }
    }
}
