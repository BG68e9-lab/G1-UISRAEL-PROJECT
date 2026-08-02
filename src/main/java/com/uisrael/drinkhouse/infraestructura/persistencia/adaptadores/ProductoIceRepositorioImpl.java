package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.ProductoIceHistorico;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoIceRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoIceEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoIceJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;

/**
 * El dominio ProductoIceHistorico es "plano" (solo productoId), asi que la
 * resolucion de la relacion con ProductoEntity vive aqui (mismo patron que
 * LoteProductoRepositorioImpl).
 */
public class ProductoIceRepositorioImpl implements IProductoIceRepositorio {

	private final IProductoIceJpaRepositorio jpaRepositorio;
	private final IProductoJpaRepositorio productoJpaRepositorio;

	public ProductoIceRepositorioImpl(IProductoIceJpaRepositorio jpaRepositorio,
			IProductoJpaRepositorio productoJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.productoJpaRepositorio = productoJpaRepositorio;
	}

	@Override
	public ProductoIceHistorico registrarNuevaTasa(Long productoId, BigDecimal valor, String tipoIce,
			String motivo) {
		ProductoEntity producto = productoJpaRepositorio.findById(productoId)
				.orElseThrow(() -> new IllegalArgumentException("El producto indicado no existe: " + productoId));

		OffsetDateTime ahora = OffsetDateTime.now();

		jpaRepositorio.findFirstByFkProductoEntity_ProductoIdAndVigenteHastaIsNullOrderByVigenteDesdeDesc(productoId)
				.ifPresent(vigente -> {
					vigente.setVigenteHasta(ahora);
					jpaRepositorio.save(vigente);
				});

		ProductoIceEntity nueva = new ProductoIceEntity();
		nueva.setFkProductoEntity(producto);
		nueva.setValor(valor);
		nueva.setTipoIce(tipoIce);
		nueva.setVigenteDesde(ahora);
		nueva.setMotivo(motivo);

		return toDomain(jpaRepositorio.save(nueva));
	}

	@Override
	public Optional<ProductoIceHistorico> obtenerVigente(Long productoId) {
		return jpaRepositorio
				.findFirstByFkProductoEntity_ProductoIdAndVigenteHastaIsNullOrderByVigenteDesdeDesc(productoId)
				.map(this::toDomain);
	}

	@Override
	public List<ProductoIceHistorico> listarHistorial(Long productoId) {
		return jpaRepositorio.findByFkProductoEntity_ProductoIdOrderByVigenteDesdeDesc(productoId).stream()
				.map(this::toDomain).toList();
	}

	private ProductoIceHistorico toDomain(ProductoIceEntity entity) {
		ProductoIceHistorico historico = new ProductoIceHistorico();
		historico.setId(entity.getId());
		historico.setProductoId(entity.getFkProductoEntity() != null ? entity.getFkProductoEntity().getProductoId() : null);
		historico.setTipoIce(entity.getTipoIce());
		historico.setValor(entity.getValor());
		historico.setVigenteDesde(entity.getVigenteDesde());
		historico.setVigenteHasta(entity.getVigenteHasta());
		historico.setMotivo(entity.getMotivo());
		historico.setCreadoEn(entity.getCreadoEn());
		return historico;
	}
}
