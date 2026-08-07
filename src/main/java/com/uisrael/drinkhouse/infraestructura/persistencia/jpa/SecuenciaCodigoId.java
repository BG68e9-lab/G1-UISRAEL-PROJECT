package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.util.Objects;

public class SecuenciaCodigoId implements Serializable {

    private NegocioEntity negocio;
    private TipoMovimientoEntity tipoMovimiento;

    public SecuenciaCodigoId() {}

    public SecuenciaCodigoId(NegocioEntity negocio, TipoMovimientoEntity tipoMovimiento) {
        this.negocio = negocio;
        this.tipoMovimiento = tipoMovimiento;
    }

    public NegocioEntity getNegocio() {
        return negocio;
    }

    public void setNegocio(NegocioEntity negocio) {
        this.negocio = negocio;
    }

    public TipoMovimientoEntity getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimientoEntity tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecuenciaCodigoId)) return false;
        SecuenciaCodigoId that = (SecuenciaCodigoId) o;
        
        Integer thisNegocioId = (negocio != null) ? negocio.getNegocioId() : null;
        Integer thatNegocioId = (that.negocio != null) ? that.negocio.getNegocioId() : null;
        Integer thisTipoId = (tipoMovimiento != null) ? tipoMovimiento.getTipoMovimientoId() : null;
        Integer thatTipoId = (that.tipoMovimiento != null) ? that.tipoMovimiento.getTipoMovimientoId() : null;
        
        return Objects.equals(thisNegocioId, thatNegocioId) &&
               Objects.equals(thisTipoId, thatTipoId);
    }

@Override
    public int hashCode() {
        Integer negocioId = (negocio != null) ? negocio.getNegocioId() : null;
        Integer tipoId = (tipoMovimiento != null) ? tipoMovimiento.getTipoMovimientoId() : null;
        return Objects.hash(negocioId, tipoId);
    }
}
