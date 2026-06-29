package com.uisrael.drinkhouse.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// --- Imports de Casos de Uso (Interfaces e Implementaciones) ---
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoRespaldoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoMovimientoUseCase;

import com.uisrael.drinkhouse.aplicacion.casosuso.impl.EstadoRespaldoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.LoteProductoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.MovimientoInventarioUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.ProductoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.ProveedorUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.SecuenciaCodigoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.TipoMovimientoUseCaseImpl;

// --- Imports de Repositorios (Dominio y Adaptadores) ---
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoRespaldoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProveedorRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ISecuenciaCodigoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;

import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.EstadoRespaldoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.LoteProductoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.MovimientoInventarioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.ProductoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.ProveedorRepositoriImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.SecuenciaCodigoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.TipoMovimientoRepositorioImpl;

// --- Imports de Mappers y JPA Repositorios ---
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoRespaldoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILoteProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IMovimientoInventarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProveedorJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ISecuenciaCodigoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITipoMovimientoJpaMapper;

import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoRespaldoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IMovimientoInventarioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProveedorJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ISecuenciaCodigoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoMovimientoJpaRepositorio;

@Configuration
public class DrinkHouseConfig {

	// ==================== PRODUCTO ====================
	@Bean
	IProductoRepositorio productoRepositorio(IProductoJpaRepositorio jpaRepositorio, IProductoJpaMapper mapper) {
		return new ProductoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IProductoUseCase productoUseCase(IProductoRepositorio repoUseCase) {
		return new ProductoUseCaseImpl(repoUseCase);
	}

	// ==================== PROVEEDOR ====================
	@Bean
	IProveedorRepositorio proveedorRepositorio(IProveedorJpaRepositorio jpaRepositorio, IProveedorJpaMapper mapper) {
		return new ProveedorRepositoriImpl(jpaRepositorio, mapper);
	}

	@Bean
	IProveedorUseCase proveedorUseCase(IProveedorRepositorio repoUseCase) {
		return new ProveedorUseCaseImpl(repoUseCase);
	}

	// ==================== LOTE PRODUCTO ====================
	@Bean
	ILoteProductoRepositorio loteProductoRepositorio(ILoteProductoJpaRepositorio jpaRepositorio, ILoteProductoJpaMapper mapper) {
		return new LoteProductoRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	ILoteProductoUseCase loteProductoUseCase(ILoteProductoRepositorio repoUseCase) {
		return new LoteProductoUseCaseImpl(repoUseCase);
	}
	
	// ==================== MOVIMIENTO INVENTARIO ====================
	@Bean
	IMovimientoInventarioRepositorio movimientoInventarioRepositorio(IMovimientoInventarioJpaRepositorio jpaRepositorio, IMovimientoInventarioJpaMapper mapper) {
		return new MovimientoInventarioRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	IMovimientoInventarioUseCase movimientoInventarioUseCase(IMovimientoInventarioRepositorio repoUseCase) {
		return new MovimientoInventarioUseCaseImpl(repoUseCase);
	}

	// ==================== ESTADO RESPALDO ====================
	@Bean
	IEstadoRespaldoRepositorio estadoRespaldoRepositorio(IEstadoRespaldoJpaRepositorio jpaRepositorio, IEstadoRespaldoJpaMapper mapper) {
		return new EstadoRespaldoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IEstadoRespaldoUseCase estadoRespaldoUseCase(IEstadoRespaldoRepositorio repoUseCase) {
		return new EstadoRespaldoUseCaseImpl(repoUseCase);
	}

	// ==================== SECUENCIA CODIGO ====================
	@Bean
	ISecuenciaCodigoRepositorio secuenciaCodigoRepositorio(ISecuenciaCodigoJpaRepositorio jpaRepositorio, ISecuenciaCodigoJpaMapper mapper) {
		return new SecuenciaCodigoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ISecuenciaCodigoUseCase secuenciaCodigoUseCase(ISecuenciaCodigoRepositorio repoUseCase) {
		return new SecuenciaCodigoUseCaseImpl(repoUseCase);
	}

	// ==================== TIPO MOVIMIENTO ====================
	@Bean
	ITipoMovimientoRepositorio tipoMovimientoRepositorio(ITipoMovimientoJpaRepositorio jpaRepositorio, ITipoMovimientoJpaMapper mapper) {
		return new TipoMovimientoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ITipoMovimientoUseCase tipoMovimientoUseCase(ITipoMovimientoRepositorio repoUseCase) {
		return new TipoMovimientoUseCaseImpl(repoUseCase);
	}
}