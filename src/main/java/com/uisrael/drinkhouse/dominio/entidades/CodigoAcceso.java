package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CodigoAcceso {

	private UUID codigoAccesoId;
	private String tipoCodigo;
	private String codigoHash;
	private OffsetDateTime expiraEn;
	private Boolean usado;
	private OffsetDateTime usadoEn;
	private OffsetDateTime creadoEn;

	public CodigoAcceso() {
	}

	public CodigoAcceso(UUID codigoAccesoId, String tipoCodigo, String codigoHash,
			OffsetDateTime expiraEn, Boolean usado, OffsetDateTime usadoEn, OffsetDateTime creadoEn) {
		this.codigoAccesoId = codigoAccesoId;
		this.tipoCodigo = tipoCodigo;
		this.codigoHash = codigoHash;
		this.expiraEn = expiraEn;
		this.usado = usado;
		this.usadoEn = usadoEn;
		this.creadoEn = creadoEn;
	}

	public UUID getCodigoAccesoId() { return codigoAccesoId; }
	public void setCodigoAccesoId(UUID codigoAccesoId) { this.codigoAccesoId = codigoAccesoId; }

	public String getTipoCodigo() { return tipoCodigo; }
	public void setTipoCodigo(String tipoCodigo) { this.tipoCodigo = tipoCodigo; }

	public String getCodigoHash() { return codigoHash; }
	public void setCodigoHash(String codigoHash) { this.codigoHash = codigoHash; }

	public OffsetDateTime getExpiraEn() { return expiraEn; }
	public void setExpiraEn(OffsetDateTime expiraEn) { this.expiraEn = expiraEn; }

	public Boolean getUsado() { return usado; }
	public void setUsado(Boolean usado) { this.usado = usado; }

	public OffsetDateTime getUsadoEn() { return usadoEn; }
	public void setUsadoEn(OffsetDateTime usadoEn) { this.usadoEn = usadoEn; }

	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}
