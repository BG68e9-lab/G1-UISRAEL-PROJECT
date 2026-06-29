package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;

public interface ILogAuditoriaUseCase {
	
	LogAuditoria guardar(LogAuditoria nuevoLogAuditoria);
	
	LogAuditoria buscarPorId(Long idLogAuditoria);
	
	List<LogAuditoria> listarTodos();
	
	void eliminar(Long idLogAuditoria);

}
