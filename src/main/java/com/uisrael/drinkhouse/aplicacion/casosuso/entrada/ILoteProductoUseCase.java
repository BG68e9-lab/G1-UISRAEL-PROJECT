package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

public interface ILoteProductoUseCase {

	LoteProducto crearLote(LoteProducto lote, Long productoId);

	List<LoteProducto> buscarPorProducto(Long productoId);

	LoteProducto buscarPorId(Long id);

	Page<LoteProducto> listarPaginado(Pageable pageable);

	List<LoteProducto> buscarProximosAVencer(int dias);
}
