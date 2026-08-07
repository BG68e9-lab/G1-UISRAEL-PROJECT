package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;

public interface ISecuenciaCodigoUseCase {

	Long siguiente(Integer negocioId, Integer tipoMovimientoId);

	List<SecuenciaCodigo> listarTodas();

	List<SecuenciaCodigo> listarPorNegocio(Integer negocioId);

	SecuenciaCodigo buscar(Integer negocioId, Integer tipoMovimientoId);

	SecuenciaCodigo crear(SecuenciaCodigo secuencia);

	SecuenciaCodigo actualizar(Integer negocioId, Integer tipoMovimientoId, Long nuevoNumero);

	void eliminar(Integer negocioId, Integer tipoMovimientoId);

	SecuenciaCodigo reiniciar(Integer negocioId, Integer tipoMovimientoId, Long valorInicial);

	int inicializarSecuenciasParaTodosLosNegocios();

	default Long siguiente(String tipo) {
		return siguiente(1, 1);
	}
}
