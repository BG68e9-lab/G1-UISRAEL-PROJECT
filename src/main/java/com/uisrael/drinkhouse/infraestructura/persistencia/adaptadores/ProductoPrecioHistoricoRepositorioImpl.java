package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.ProductoIceHistorico;
import com.uisrael.drinkhouse.dominio.entidades.ProductoPrecioHistorico;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoPrecioHistoricoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoPrecioHistoricoEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoPrecioHistoricoJpaRepositorio;

/**
 * Calcula y guarda el snapshot consolidado de precio + impuestos de un
 * producto. El calculo de impuestos sigue el orden usado en Ecuador: el ICE
 * se aplica primero sobre el precio de venta (porcentual o monto especifico),
 * y el IVA se aplica despues sobre (precioVenta + ICE).
 */
public class ProductoPrecioHistoricoRepositorioImpl implements IProductoPrecioHistoricoRepositorio {

	private final IProductoPrecioHistoricoJpaRepositorio jpaRepositorio;
	private final IProductoJpaRepositorio productoJpaRepositorio;

	public ProductoPrecioHistoricoRepositorioImpl(IProductoPrecioHistoricoJpaRepositorio jpaRepositorio,
			IProductoJpaRepositorio productoJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.productoJpaRepositorio = productoJpaRepositorio;
	}

	@Override
	public ProductoPrecioHistorico registrarSnapshot(Long productoId, BigDecimal costoPromedio,
			BigDecimal margenGanancia, BigDecimal precioVenta, BigDecimal ivaPorcentajeAplicado,
			String iceTipoAplicado, BigDecimal iceValorAplicado, String motivo) {

		ProductoEntity producto = productoJpaRepositorio.findById(productoId)
				.orElseThrow(() -> new IllegalArgumentException("El producto indicado no existe: " + productoId));

		OffsetDateTime ahora = OffsetDateTime.now();

		jpaRepositorio.findFirstByFkProductoEntity_ProductoIdAndVigenteHastaIsNullOrderByVigenteDesdeDesc(productoId)
				.ifPresent(vigente -> {
					vigente.setVigenteHasta(ahora);
					jpaRepositorio.save(vigente);
				});

		BigDecimal iceMonto = calcularIceMonto(precioVenta, iceTipoAplicado, iceValorAplicado);
		BigDecimal baseIva = precioVenta.add(iceMonto);
		BigDecimal ivaPct = ivaPorcentajeAplicado != null ? ivaPorcentajeAplicado : BigDecimal.ZERO;
		BigDecimal ivaMonto = baseIva.multiply(ivaPct).divide(BigDecimal.valueOf(100));
		BigDecimal precioFinal = baseIva.add(ivaMonto).setScale(2, RoundingMode.HALF_UP);

		ProductoPrecioHistoricoEntity nueva = new ProductoPrecioHistoricoEntity();
		nueva.setFkProductoEntity(producto);
		nueva.setCostoPromedio(costoPromedio);
		nueva.setMargenGanancia(margenGanancia);
		nueva.setPrecioVenta(precioVenta);
		nueva.setIvaPorcentajeAplicado(ivaPorcentajeAplicado);
		nueva.setIceTipoAplicado(iceTipoAplicado);
		nueva.setIceValorAplicado(iceValorAplicado);
		nueva.setPrecioFinalConImpuestos(precioFinal);
		nueva.setVigenteDesde(ahora);
		nueva.setMotivo(motivo);

		return toDomain(jpaRepositorio.save(nueva));
	}

	private BigDecimal calcularIceMonto(BigDecimal precioVenta, String iceTipoAplicado, BigDecimal iceValorAplicado) {
		if (iceTipoAplicado == null || iceValorAplicado == null) {
			return BigDecimal.ZERO;
		}
		if (ProductoIceHistorico.TIPO_ESPECIFICO.equals(iceTipoAplicado)) {
			return iceValorAplicado;
		}
		// PORCENTUAL (ad-valorem): % sobre el precio de venta
		return precioVenta.multiply(iceValorAplicado).divide(BigDecimal.valueOf(100));
	}

	@Override
	public Optional<ProductoPrecioHistorico> obtenerVigente(Long productoId) {
		return jpaRepositorio
				.findFirstByFkProductoEntity_ProductoIdAndVigenteHastaIsNullOrderByVigenteDesdeDesc(productoId)
				.map(this::toDomain);
	}

	@Override
	public List<ProductoPrecioHistorico> listarHistorial(Long productoId) {
		return jpaRepositorio.findByFkProductoEntity_ProductoIdOrderByVigenteDesdeDesc(productoId).stream()
				.map(this::toDomain).toList();
	}

	private ProductoPrecioHistorico toDomain(ProductoPrecioHistoricoEntity entity) {
		ProductoPrecioHistorico historico = new ProductoPrecioHistorico();
		historico.setId(entity.getId());
		historico.setProductoId(entity.getFkProductoEntity() != null ? entity.getFkProductoEntity().getProductoId() : null);
		historico.setCostoPromedio(entity.getCostoPromedio());
		historico.setMargenGanancia(entity.getMargenGanancia());
		historico.setPrecioVenta(entity.getPrecioVenta());
		historico.setIvaPorcentajeAplicado(entity.getIvaPorcentajeAplicado());
		historico.setIceTipoAplicado(entity.getIceTipoAplicado());
		historico.setIceValorAplicado(entity.getIceValorAplicado());
		historico.setPrecioFinalConImpuestos(entity.getPrecioFinalConImpuestos());
		historico.setVigenteDesde(entity.getVigenteDesde());
		historico.setVigenteHasta(entity.getVigenteHasta());
		historico.setMotivo(entity.getMotivo());
		historico.setCreadoEn(entity.getCreadoEn());
		return historico;
	}
}
