package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;

public interface IAjusteInventarioAuditoriaUseCase {

AjusteInventarioAuditoria buscarPorMovimiento(Long movimientoId);

}
