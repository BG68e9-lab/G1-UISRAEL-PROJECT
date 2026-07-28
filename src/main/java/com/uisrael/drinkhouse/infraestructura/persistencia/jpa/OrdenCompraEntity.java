package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Data
@Entity
@Table(name = "ordenes_compra")
public class OrdenCompraEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orden_compra_id")
	private Long ordenCompraId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@ManyToOne
	@JoinColumn(name = "proveedor_id")
	private ProveedorEntity fkProveedorEntity;

	@ManyToOne
	@JoinColumn(name = "estado_oc_id")
	private EstadoOcEntity fkEstadoOcEntity;

	@Column(name = "codigo_referencia", nullable = false, length = 50)
	private String codigoReferencia;

	@Column(name = "total", nullable = false, precision = 12, scale = 2)
	private BigDecimal total;

	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "usuario_creacion", length = 100)
	private String usuarioCreacion;

	@Column(name = "observaciones", length = 500)
	private String observaciones;

	@Version
	@Column(name = "version")
	private Long version;

	@OneToMany(mappedBy = "fkOrdenCompraEntity", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleOrdenCompraEntity> detalles = new ArrayList<>();

	@OneToMany(mappedBy = "fkOrdenCompraEntity")
	private List<LoteProductoEntity> lotes = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		if (this.fechaCreacion == null) {
			this.fechaCreacion = LocalDateTime.now();
		}
	}
}
