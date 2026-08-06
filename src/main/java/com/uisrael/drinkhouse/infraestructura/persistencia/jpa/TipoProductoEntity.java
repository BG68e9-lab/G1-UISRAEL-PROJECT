package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tipos_producto")
public class TipoProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tipo_producto_id")
	private Long tipoProductoId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id", nullable = false)
	@JsonIgnore
	private CategoriaEntity fkCategoriaEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "negocio_id")
	@JsonIgnore
	private NegocioEntity fkNegocioEntity;

	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	@Column(name = "descripcion", length = 255)
	private String descripcion;

	@Column(name = "activo", nullable = false)
	private Boolean activo;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@Column(name = "actualizado_en", nullable = false)
	private OffsetDateTime actualizadoEn;

	@OneToMany(mappedBy = "fkTipoProductoEntity", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<ProductoEntity> productos = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
		this.actualizadoEn = OffsetDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.actualizadoEn = OffsetDateTime.now();
	}
}
