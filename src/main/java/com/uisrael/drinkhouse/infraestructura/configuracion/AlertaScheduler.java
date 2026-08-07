package com.uisrael.drinkhouse.infraestructura.configuracion;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAlertaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;

@Component
public class AlertaScheduler {

    private final IAlertaUseCase alertaUseCase;
    private final ILoteProductoRepositorio loteRepositorio;

public AlertaScheduler(IAlertaUseCase alertaUseCase, ILoteProductoRepositorio loteRepositorio) {
        this.alertaUseCase = alertaUseCase;
        this.loteRepositorio = loteRepositorio;
    }

@Scheduled(cron = "0 0 7 * * *")
    public void verificarVencimientos() {
        LocalDate limite = LocalDate.now().plusDays(7);
        List<LoteProducto> lotes = loteRepositorio.buscarProximosAVencer(limite);
        lotes.forEach(lote -> alertaUseCase.crearAlertaVencimientoProximo(lote));
    }
}
