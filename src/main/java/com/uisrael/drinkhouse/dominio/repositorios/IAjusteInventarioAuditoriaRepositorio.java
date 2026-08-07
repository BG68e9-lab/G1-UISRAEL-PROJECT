package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;

public interface IAjusteInventarioAuditoriaRepositorio {

AjusteInventarioAuditoria guardar(AjusteInventarioAuditoria auditoria);

Optional<AjusteInventarioAuditoria> buscarPorMovimiento(Long movimientoId);
}
