package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAlertaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.response.AlertaResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IAlertaDtoMapper;

@RestController
@RequestMapping("/api/v1/alertas")
public class AlertaController {

	private final IAlertaUseCase alertaUseCase;
	private final IAlertaDtoMapper mapper;

	public AlertaController(IAlertaUseCase alertaUseCase, IAlertaDtoMapper mapper) {
		this.alertaUseCase = alertaUseCase;
		this.mapper = mapper;
	}

	/** GET /api/v1/alertas?tipoAlerta=...&atendida=... */
	@GetMapping
	public ResponseEntity<List<AlertaResponseDto>> listar(
			@RequestParam(required = false) String tipoAlerta,
			@RequestParam(required = false) Boolean atendida) {
		List<AlertaResponseDto> lista = alertaUseCase.listarConFiltros(tipoAlerta, atendida)
				.stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	/** PATCH /api/v1/alertas/{id}/atender */
	@PatchMapping("/{id}/atender")
	public ResponseEntity<AlertaResponseDto> marcarComoAtendida(@PathVariable Long id) {
		return ResponseEntity.ok(mapper.toResponseDto(alertaUseCase.marcarComoAtendida(id)));
	}

	/** GET /api/v1/alertas/no-atendidas/conteo */
	@GetMapping("/no-atendidas/conteo")
	public ResponseEntity<Long> contarNoAtendidas() {
		return ResponseEntity.ok(alertaUseCase.contarNoAtendidas());
	}
}
