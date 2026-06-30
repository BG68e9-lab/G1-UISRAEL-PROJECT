package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CodigoAccesoResponseDto {

	private UUID codigoAccesoId;
	private UUID usuarioId;
	private String tipoCodigo;
	private Boolean usado;
	private OffsetDateTime usadoEn;
	private OffsetDateTime expiraEn;
	private OffsetDateTime creadoEn;

	public UUID getCodigoAccesoId() {
		return codigoAccesoId;
	}

	public void setCodigoAccesoId(UUID codigoAccesoId) {
		this.codigoAccesoId = codigoAccesoId;
	}

	public UUID getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(UUID usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getTipoCodigo() {
		return tipoCodigo;
	}

	public void setTipoCodigo(String tipoCodigo) {
		this.tipoCodigo = tipoCodigo;
	}

	public Boolean getUsado() {
		return usado;
	}

	public void setUsado(Boolean usado) {
		this.usado = usado;
	}

	public OffsetDateTime getUsadoEn() {
		return usadoEn;
	}

	public void setUsadoEn(OffsetDateTime usadoEn) {
		this.usadoEn = usadoEn;
	}

	public OffsetDateTime getExpiraEn() {
		return expiraEn;
	}

	public void setExpiraEn(OffsetDateTime expiraEn) {
		this.expiraEn = expiraEn;
	}

	public OffsetDateTime getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(OffsetDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}
}