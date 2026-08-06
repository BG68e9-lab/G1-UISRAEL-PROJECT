package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.especificaciones.ProductoSpecification;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;

public class ProductoRepositorioImpl implements IProductoRepositorio {

	private final IProductoJpaRepositorio jpaRepositorio;
	private final IProductoJpaMapper productoMapper;

	public ProductoRepositorioImpl(IProductoJpaRepositorio jpaRepositorio, IProductoJpaMapper productoMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.productoMapper = productoMapper;
	}

	@Override
	public Producto guardar(Producto producto) {
		System.out.println("=== REPOSITORIO (ANTES toEntity): producto.tipoProductoId = " + producto.getTipoProductoId());
		
		ProductoEntity entity;
		if (producto.getProductoId() != null) {
			entity = jpaRepositorio.findById(producto.getProductoId())
				.orElseThrow(() -> new RecursoNoEncontradoException(
					"Producto no encontrado con ID: " + producto.getProductoId()));
			
			if (producto.getNegocioId() != null) {
				NegocioEntity negocioEntity = new NegocioEntity();
				negocioEntity.setNegocioId(producto.getNegocioId());
				entity.setFkNegocioEntity(negocioEntity);
			}
			
			if (producto.getCategoriaId() != null) {
				CategoriaEntity categoriaEntity = new CategoriaEntity();
				categoriaEntity.setCategoriaId(producto.getCategoriaId());
				entity.setFkCategoriaEntity(categoriaEntity);
			}
			
			if (producto.getTipoProductoId() != null) {
				TipoProductoEntity tipoEntity = new TipoProductoEntity();
				tipoEntity.setTipoProductoId(producto.getTipoProductoId());
				entity.setFkTipoProductoEntity(tipoEntity);
			}
			
			entity.setNombre(producto.getNombre());
			entity.setMarca(producto.getMarca());
			entity.setDescripcion(producto.getDescripcion());
			entity.setCostoPromedio(producto.getCostoPromedio());
			entity.setMargenGanancia(producto.getMargenGanancia());
			entity.setPrecioVenta(producto.getPrecioVenta());
			entity.setPrecioPersonalizado(producto.getPrecioPersonalizado());
			entity.setStockActual(producto.getStockActual());
			entity.setStockMinimo(producto.getStockMinimo());
			entity.setVisibleSinStock(producto.getVisibleSinStock());
			entity.setPermiteStockNegativo(producto.getPermiteStockNegativo());
			entity.setOrigenIdentificacion(producto.getOrigenIdentificacion());
			entity.setActivo(producto.getActivo());
			
			System.out.println("=== REPOSITORIO (UPDATE): Actualizando entidad existente con version = " + entity.getVersion());
		} else {
			entity = productoMapper.toEntity(producto);
			System.out.println("=== REPOSITORIO (CREATE): Nueva entidad");
		}
		
		System.out.println("=== REPOSITORIO (DESPUES toEntity): entity.fkTipoProductoEntity = " + entity.getFkTipoProductoEntity());
		if (entity.getFkTipoProductoEntity() != null) {
			System.out.println("=== REPOSITORIO: fkTipoProductoEntity.tipoProductoId = " + entity.getFkTipoProductoEntity().getTipoProductoId());
		}
		
		ProductoEntity guardado = jpaRepositorio.save(entity);
		ProductoEntity guardadoConRelaciones = jpaRepositorio.findByIdWithRelations(guardado.getProductoId()).orElse(guardado);
		return productoMapper.toDomain(guardadoConRelaciones);
	}

	@Override
	public Optional<Producto> buscarPorId(Long id) {
		return jpaRepositorio.findByIdWithRelations(id).map(productoMapper::toDomain);
	}

	@Override
	public List<Producto> listarTodos() {
		return jpaRepositorio.findAllWithRelations().stream().map(productoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(Long id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public boolean existePorNombre(String nombre) {
		return jpaRepositorio.existsByNombre(nombre);
	}

	@Override
	public List<Producto> buscarConFiltros(String nombre, String marca, Long tipoProductoId, Long categoriaId) {
		return jpaRepositorio.findAll(ProductoSpecification.conFiltros(nombre, marca, tipoProductoId, categoriaId))
				.stream().map(productoMapper::toDomain).toList();
	}

	@Override
	public Producto lockForUpdate(Long productoId) {
		ProductoEntity entity = jpaRepositorio.findByIdForUpdate(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con ID: " + productoId));
		return productoMapper.toDomain(entity);
	}

	@Override
	public void actualizarStock(Long productoId, BigDecimal newStock) {
		ProductoEntity entity = jpaRepositorio.findById(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con ID: " + productoId));
		
		entity.setStockActual(newStock.intValue());
		jpaRepositorio.save(entity);
	}
}
