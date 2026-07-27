package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.response.LogAuditoriaResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ILogAuditoriaDtoMapper;

@RestController
@RequestMapping("/api/v1/auditoria")
public class LogAuditoriaController {

	private final ILogAuditoriaUseCase logAuditoriaUseCase;
	private final ILogAuditoriaDtoMapper mapper;

	public LogAuditoriaController(ILogAuditoriaUseCase logAuditoriaUseCase, ILogAuditoriaDtoMapper mapper) {
		this.logAuditoriaUseCase = logAuditoriaUseCase;
		this.mapper = mapper;
	}

	@GetMapping
	public List<LogAuditoriaResponseDto> buscarConFiltros(
			@RequestParam(required = false) String entidad,
			@RequestParam(required = false) String accion,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
		return logAuditoriaUseCase.buscarConFiltros(entidad, accion, desde, hasta)
				.stream()
				.map(mapper::toResponseDto)
				.toList();
	}

	@GetMapping("/entidad/{entidadId}")
	public List<LogAuditoriaResponseDto> buscarPorEntidadId(@PathVariable String entidadId) {
		return logAuditoriaUseCase.buscarPorEntidadId(entidadId)
				.stream()
				.map(mapper::toResponseDto)
				.toList();
	}

}
