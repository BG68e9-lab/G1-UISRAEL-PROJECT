package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase de clave primaria compuesta para SecuenciaCodigoEntity.
 */
public class SecuenciaCodigoId implements Serializable {

    private NegocioEntity negocio;
    private TipoMovimientoEntity tipoMovimiento;

    public SecuenciaCodigoId() {}

    public SecuenciaCodigoId(NegocioEntity negocio, TipoMovimientoEntity tipoMovimiento) {
        this.negocio = negocio;
        this.tipoMovimiento = tipoMovimiento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecuenciaCodigoId)) return false;
        SecuenciaCodigoId that = (SecuenciaCodigoId) o;
        return Objects.equals(negocio, that.negocio) &&
               Objects.equals(tipoMovimiento, that.tipoMovimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(negocio, tipoMovimiento);
    }
}
