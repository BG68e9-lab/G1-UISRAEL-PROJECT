package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proveedores")
public class ProveedorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proveedor_id")
	private Integer proveedorId;
	@Column(name = "negocio_id", nullable = false)
	private Integer negocioId;
	@Column(name = "ruc", nullable = false, length = 20)
	private String ruc;
	@Column(name = "razon_social", nullable = false, length = 20)
	private String razonSocial;
	@Column(name = "direccion", nullable = false, length = 50)
	private String direccion;
	@Column(name = "telefono", nullable = false, length = 15)
	private String telefono;
	@Column(name = "email", nullable = false, length = 20)
	private String email;

}
