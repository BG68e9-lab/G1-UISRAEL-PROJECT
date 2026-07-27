package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;

public interface ILogAuditoriaUseCase {

	LogAuditoria registrar(String entidad, String entidadId, String accion, Object detalle);

	List<LogAuditoria> buscarConFiltros(String entidad, String accion, OffsetDateTime desde, OffsetDateTime hasta);

	List<LogAuditoria> buscarPorEntidadId(String entidadId);

}
