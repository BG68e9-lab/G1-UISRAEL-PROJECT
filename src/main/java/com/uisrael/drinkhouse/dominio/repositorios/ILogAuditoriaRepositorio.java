package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;

public interface ILogAuditoriaRepositorio {

	LogAuditoria guardar(LogAuditoria log);

	List<LogAuditoria> buscarConFiltros(String entidad, String accion, OffsetDateTime desde, OffsetDateTime hasta);

	List<LogAuditoria> buscarPorEntidadId(String entidadId);

}
