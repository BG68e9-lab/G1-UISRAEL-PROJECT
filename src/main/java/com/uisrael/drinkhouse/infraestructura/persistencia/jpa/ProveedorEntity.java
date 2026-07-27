package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedores")
public class ProveedorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proveedor_id")
	private Long proveedorId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@Column(name = "ruc", nullable = false, length = 13, unique = true)
	private String ruc;

	@Column(name = "razon_social", nullable = false, length = 150)
	private String razonSocial;

	@Column(name = "direccion", length = 255)
	private String direccion;

	@Column(name = "telefono", length = 20)
	private String telefono;

	@Column(name = "email", nullable = false, length = 150)
	private String email;
}
