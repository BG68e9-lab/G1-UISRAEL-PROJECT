package com.uisrael.drinkhouse.infraestructura.configuracion;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAlertaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;

/**
 * Componente programador que ejecuta verificaciones periódicas de vencimientos.
 * Corre diariamente a las 07:00 y crea alertas de tipo VENCIMIENTO_PROXIMO
 * para los lotes que venzan en los próximos 7 días.
 */
@Component
public class AlertaScheduler {

    private final IAlertaUseCase alertaUseCase;
    private final ILoteProductoRepositorio loteRepositorio;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param alertaUseCase    caso de uso de alertas para crear alertas de vencimiento
     * @param loteRepositorio  repositorio de lotes para consultar próximos a vencer
     */
    public AlertaScheduler(IAlertaUseCase alertaUseCase, ILoteProductoRepositorio loteRepositorio) {
        this.alertaUseCase = alertaUseCase;
        this.loteRepositorio = loteRepositorio;
    }

    /**
     * Verifica diariamente a las 07:00 los lotes próximos a vencer.
     * Por cada lote cuya fechaVencimiento sea menor o igual a los próximos 7 días
     * y cuya cantidadDisponible sea mayor a cero, crea una alerta de tipo VENCIMIENTO_PROXIMO.
     * Requisito 12.2
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void verificarVencimientos() {
        LocalDate limite = LocalDate.now().plusDays(7);
        List<LoteProducto> lotes = loteRepositorio.buscarProximosAVencer(limite);
        lotes.forEach(lote -> alertaUseCase.crearAlertaVencimientoProximo(lote));
    }
}
