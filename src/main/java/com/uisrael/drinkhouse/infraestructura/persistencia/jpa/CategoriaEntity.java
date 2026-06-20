package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categorias")
public class CategoriaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "categoria_id")
	private Integer categoriaId;
	@ManyToOne
	@JoinColumn(name = "negocio_id", nullable = false)
	private NegocioEntity negocioId;
	@Column(name = "nombre", nullable = false, length = 60)
	private String nombre;
	@Column(name = "margen_ganancia_pct", precision = 5, scale = 2)
	private BigDecimal margenGananciaPct;
	@Column(name = "activo", nullable = false)
	private Boolean activo;
	
}
