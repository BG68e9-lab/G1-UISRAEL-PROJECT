package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens_ia_negocio")
public class TokensIaNegocioEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "token_ia_negocio_id")
	private Long tokenIaNegocioId;
	
	@ManyToOne
	@JoinColumn(name = "negocio_id", nullable = false)
	private NegocioEntity negocioId;
	
	@Column(name = "clave_configuracion", nullable = false, unique = true, length = 100)
	private String claveConfiguracion;
	
	@Column(name = "valor_token", nullable = false, length = 255)
	private String valorToken;
	
	@Column(name = "registrado_en", updatable = false, nullable = false)
	private OffsetDateTime registradoEn;
	
	@PrePersist
	protected void onCreate() {
		this.registradoEn = OffsetDateTime.now();
	}
}