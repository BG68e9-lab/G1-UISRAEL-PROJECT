package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	@Column(name = "numero_oc", nullable = false, length = 50)
	private String numeroOc;

	@Column(name = "fecha_oc", nullable = false)
	private LocalDate fechaOc;

	@Column(name = "total_oc", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalOc;

	@Column(name = "documento_url", length = 500)
	private String documentoUrl;

	@Column(name = "extraido_por_ia", nullable = false)
	private Boolean extraidoPorIa;

	@Column(name = "confirmado_por")
	private UUID confirmadoPor;

	@Column(name = "confirmado_en")
	private OffsetDateTime confirmadoEn;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@OneToMany(mappedBy = "fkOrdenCompraEntity")
	private List<LoteProductoEntity> lotes = new ArrayList<>();

	@OneToMany(mappedBy = "fkOrdenCompraEntity")
	private List<IdentificacionIaEntity> identificaciones = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
		if (this.extraidoPorIa == null) this.extraidoPorIa = false;
	}
}
