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
import com.uisrael.drinkhouse.presentacion.dto.response.ReporteConsumoIaMensualDto;

/**
 * Servicio para generar y enviar reportes de consumo de IA mensual.
 * Se ejecuta automáticamente el primer día de cada mes a las 8:00 AM.
 */
@Service
public class ReporteConsumoIaService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteConsumoIaService.class);

    private final IConsumoIaMensualJpaRepositorio consumoRepositorio;

    public ReporteConsumoIaService(IConsumoIaMensualJpaRepositorio consumoRepositorio) {
        this.consumoRepositorio = consumoRepositorio;
    }

    /**
     * Genera el reporte de consumo de IA para un período específico.
     *
     * @param periodo fecha del período (primer día del mes)
     * @return lista de reportes por negocio
     */
    public List<ReporteConsumoIaMensualDto> generarReporte(LocalDate periodo) {
        logger.info("Generando reporte de consumo IA para período: {}", periodo);

        List<ConsumoIaMensualEntity> consumos = consumoRepositorio.findAll();

        return consumos.stream()
                .filter(c -> c.getPeriodo().equals(periodo))
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el reporte del mes actual.
     *
     * @return lista de reportes por negocio del mes en curso
     */
    public List<ReporteConsumoIaMensualDto> obtenerReporteMesActual() {
        LocalDate periodoActual = LocalDate.now().withDayOfMonth(1);
        return generarReporte(periodoActual);
    }

    /**
     * Obtiene el reporte del mes anterior.
     *
     * @return lista de reportes por negocio del mes pasado
     */
    public List<ReporteConsumoIaMensualDto> obtenerReporteMesAnterior() {
        LocalDate periodoAnterior = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        return generarReporte(periodoAnterior);
    }

    /**
     * Tarea programada que se ejecuta el primer día de cada mes a las 8:00 AM.
     * Genera y envía el reporte del mes anterior.
     */
    @Scheduled(cron = "0 0 8 1 * ?") // Día 1 de cada mes a las 8:00 AM
    public void generarReporteMensualAutomatico() {
        logger.info("Ejecutando generación automática de reporte mensual de IA");

        try {
            List<ReporteConsumoIaMensualDto> reporte = obtenerReporteMesAnterior();

            if (reporte.isEmpty()) {
                logger.info("No hay datos de consumo para reportar del mes anterior");
                return;
            }

            // Aquí se puede implementar el envío por email
            logger.info("Reporte generado exitosamente. Total de negocios: {}", reporte.size());
            
            // Calcular totales
            int totalIdentificaciones = reporte.stream()
                    .mapToInt(ReporteConsumoIaMensualDto::getCantidadIdentificaciones)
                    .sum();
            int totalTokens = reporte.stream()
                    .mapToInt(ReporteConsumoIaMensualDto::getTokensConsumidos)
                    .sum();

            logger.info("Totales del período: {} identificaciones, {} tokens consumidos",
                    totalIdentificaciones, totalTokens);

            // TODO: Implementar envío de email con el reporte
            // emailService.enviarReporteConsumoIa(reporte);

        } catch (Exception e) {
            logger.error("Error al generar reporte mensual automático", e);
        }
    }

    /**
     * Convierte una entidad de consumo a DTO de reporte.
     */
    private ReporteConsumoIaMensualDto convertirADto(ConsumoIaMensualEntity entity) {
        // Calcular total de tokens (input + output)
        Integer totalTokens = (entity.getTotalTokensInput() != null ? entity.getTotalTokensInput().intValue() : 0)
                + (entity.getTotalTokensOutput() != null ? entity.getTotalTokensOutput().intValue() : 0);
        
        String estadoCuota = determinarEstadoCuota(totalTokens);

        return ReporteConsumoIaMensualDto.builder()
                .consumoIaId(entity.getConsumoIaId())
                .negocioId(entity.getNegocio().getNegocioId())
                .negocioNombre(entity.getNegocio().getNombre())
                .periodo(entity.getPeriodo())
                .cantidadIdentificaciones(0) // No tenemos este campo en la entidad
                .tokensConsumidos(totalTokens)
                .estadoCuota(estadoCuota)
                .build();
    }

    /**
     * Determina el estado de la cuota según los tokens consumidos.
     */
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
