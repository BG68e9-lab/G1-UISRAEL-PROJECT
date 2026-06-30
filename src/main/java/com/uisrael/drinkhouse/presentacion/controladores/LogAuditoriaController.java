package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.LogAuditoriaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LogAuditoriaResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ILogAuditoriaDtoMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/logs-auditoria")
public class LogAuditoriaController {

	private final ILogAuditoriaUseCase logAuditoriaUseCase;
	private final ILogAuditoriaDtoMapper mapper;

	public LogAuditoriaController(ILogAuditoriaUseCase logAuditoriaUseCase, ILogAuditoriaDtoMapper mapper) {
		this.logAuditoriaUseCase = logAuditoriaUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LogAuditoriaResponseDto guardar(@Valid @RequestBody LogAuditoriaRequestDto requestDto) {
		return mapper.toResponseDto(logAuditoriaUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<LogAuditoriaResponseDto> listarTodo() {
		return logAuditoriaUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Long idLog) {
		logAuditoriaUseCase.eliminar(idLog);
		return ResponseEntity.noContent().build();
	}
}