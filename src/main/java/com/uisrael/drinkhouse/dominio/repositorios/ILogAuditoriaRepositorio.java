package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;

public interface ILogAuditoriaRepositorio {
	
	LogAuditoria guardar(LogAuditoria nuevoLogAuditoria);
	
	Optional<LogAuditoria> buscarPorId(Long idLogAuditoria);
	
	List<LogAuditoria> listarTodos();
	
	void eliminar(Long idLogAuditoria);

}
