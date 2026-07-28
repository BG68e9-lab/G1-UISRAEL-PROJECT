package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase de clave primaria compuesta para SecuenciaCodigoEntity.
 * IMPORTANTE: Los campos deben coincidir exactamente con los nombres de las propiedades @Id en SecuenciaCodigoEntity.
 * JPA usa reflexión para mapear estos campos, por lo que deben llamarse igual.
 */
public class SecuenciaCodigoId implements Serializable {

    // CRÍTICO: Estos campos deben tener el mismo nombre que las propiedades @Id en SecuenciaCodigoEntity
    private NegocioEntity negocio;
    private TipoMovimientoEntity tipoMovimiento;

    public SecuenciaCodigoId() {}

    public SecuenciaCodigoId(NegocioEntity negocio, TipoMovimientoEntity tipoMovimiento) {
        this.negocio = negocio;
        this.tipoMovimiento = tipoMovimiento;
    }

    // Getters y setters necesarios para JPA
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

    /**
     * Comparación basada SOLO en los IDs, no en toda la entidad.
     * Esto es crítico para que funcione con referencias parciales de JPA.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecuenciaCodigoId)) return false;
        SecuenciaCodigoId that = (SecuenciaCodigoId) o;
        
        // Comparar solo los IDs, no las entidades completas
        Integer thisNegocioId = (negocio != null) ? negocio.getNegocioId() : null;
        Integer thatNegocioId = (that.negocio != null) ? that.negocio.getNegocioId() : null;
        Integer thisTipoId = (tipoMovimiento != null) ? tipoMovimiento.getTipoMovimientoId() : null;
        Integer thatTipoId = (that.tipoMovimiento != null) ? that.tipoMovimiento.getTipoMovimientoId() : null;
        
        return Objects.equals(thisNegocioId, thatNegocioId) &&
               Objects.equals(thisTipoId, thatTipoId);
    }

    /**
     * Hash basado SOLO en los IDs, no en toda la entidad.
     * Esto es crítico para que funcione con referencias parciales de JPA.
     */
    @Override
    public int hashCode() {
        Integer negocioId = (negocio != null) ? negocio.getNegocioId() : null;
        Integer tipoId = (tipoMovimiento != null) ? tipoMovimiento.getTipoMovimientoId() : null;
        return Objects.hash(negocioId, tipoId);
    }
}
