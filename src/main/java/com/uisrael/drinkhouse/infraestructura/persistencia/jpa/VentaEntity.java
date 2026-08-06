package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ventas")
public class VentaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "venta_id")
	private Long ventaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "negocio_id", nullable = false)
	@JsonIgnore
	private NegocioEntity fkNegocioEntity;

	@Column(name = "codigo_venta", nullable = false, unique = true, length = 50)
	private String codigoVenta;

	@Column(name = "nombre_cliente", length = 255)
	private String nombreCliente;

	@Column(name = "cedula_ruc", length = 13)
	private String cedulaRuc;

	@Column(name = "telefono", length = 20)
	private String telefono;

	@Column(name = "email", length = 255)
	private String email;

	@Column(name = "metodo_pago", nullable = false, length = 50)
	private String metodoPago;

	@Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "descuento", precision = 10, scale = 2)
	private BigDecimal descuento = BigDecimal.ZERO;

	@Column(name = "iva", nullable = false, precision = 10, scale = 2)
	private BigDecimal iva;

	@Column(name = "total", nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Column(name = "estado", nullable = false, length = 20)
	private String estado = "COMPLETADA";

	@Column(name = "observaciones", columnDefinition = "TEXT")
	private String observaciones;

	@Column(name = "creado_en", nullable = false)
	private OffsetDateTime creadoEn;

	@Column(name = "anulado_en")
	private OffsetDateTime anuladoEn;

	@OneToMany(mappedBy = "fkVentaEntity", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleVentaEntity> detalles = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		if (creadoEn == null) {
			creadoEn = OffsetDateTime.now();
		}
	}
}
