package com.uisrael.drinkhouse.presentacion.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;

@RestController
@RequestMapping("/api/secuencias-codigo")
public class SecuenciaCodigoController {

	private final ISecuenciaCodigoUseCase secuenciaCodigoUseCase;

	public SecuenciaCodigoController(ISecuenciaCodigoUseCase secuenciaCodigoUseCase) {
		this.secuenciaCodigoUseCase = secuenciaCodigoUseCase;
	}

	@GetMapping("/{negocioId}/{tipoMovimientoId}/siguiente")
	public Long siguiente(
			@PathVariable("negocioId") Integer negocioId,
			@PathVariable("tipoMovimientoId") Integer tipoMovimientoId) {
		return secuenciaCodigoUseCase.siguiente(negocioId, tipoMovimientoId);
	}
}
