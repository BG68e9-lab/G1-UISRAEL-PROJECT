package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAlertaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Alerta;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.dominio.repositorios.IAlertaRepositorio;

public class AlertaUseCaseImpl implements IAlertaUseCase {

	private final IAlertaRepositorio repositorio;

	public AlertaUseCaseImpl(IAlertaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public void crearAlertaStockBajo(Producto producto) {
		String mensaje = "El producto " + producto.getNombre()
				+ " tiene stock " + producto.getStockActual()
				+ ", igual o por debajo del mínimo " + producto.getStockMinimo();

		Alerta alerta = new Alerta();
		alerta.setTipoAlerta("STOCK_BAJO");
		alerta.setReferenciaTipo("PRODUCTO");
		alerta.setReferenciaId(producto.getProductoId());
		alerta.setMensaje(mensaje);
		alerta.setAtendida(false);
		alerta.setCreadoEn(OffsetDateTime.now());

		repositorio.guardar(alerta);
	}

	@Override
	public void crearAlertaVencimientoProximo(LoteProducto lote) {
		String mensaje = "El lote " + lote.getCodigoEntrada()
				+ " vence el " + lote.getFechaVencimiento()
				+ " y tiene " + lote.getCantidadDisponible() + " unidades disponibles";

		Alerta alerta = new Alerta();
		alerta.setTipoAlerta("VENCIMIENTO_PROXIMO");
		alerta.setReferenciaTipo("LOTE");
		alerta.setReferenciaId(lote.getLoteId());
		alerta.setMensaje(mensaje);
		alerta.setAtendida(false);
		alerta.setCreadoEn(OffsetDateTime.now());

		repositorio.guardar(alerta);
	}

	@Override
	public List<Alerta> listarConFiltros(String tipoAlerta, Boolean atendida) {
		return repositorio.listarConFiltros(tipoAlerta, atendida);
	}

	@Override
	public Alerta marcarComoAtendida(Long alertaId) {
		Alerta alerta = repositorio.buscarPorId(alertaId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Alerta no encontrada con id: " + alertaId));
		alerta.setAtendida(true);
		return repositorio.guardar(alerta);
	}

	@Override
	public long contarNoAtendidas() {
		return repositorio.contarNoAtendidas();
	}
}
