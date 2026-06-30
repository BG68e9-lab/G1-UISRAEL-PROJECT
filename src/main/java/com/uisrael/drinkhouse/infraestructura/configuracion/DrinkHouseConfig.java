package com.uisrael.drinkhouse.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICategoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoOcUseCase;
// --- Imports de Casos de Uso (Interfaces e Implementaciones) ---
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoRespaldoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INegocioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRolUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoMovimientoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IUsuarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.CategoriaUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.CodigoAccesoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.EstadoOcUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.EstadoRespaldoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.LogAuditoriaUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.LoteProductoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.MovimientoInventarioUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.NegocioUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.ProductoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.ProveedorUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.RolUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.SecuenciaCodigoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.TipoMovimientoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.UsuarioUseCaseImpl;
import com.uisrael.drinkhouse.dominio.repositorios.ICategoriaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoOcRepositorio;
// --- Imports de Repositorios (Dominio y Adaptadores) ---
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoRespaldoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ILogAuditoriaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.INegocioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProveedorRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IRolRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ISecuenciaCodigoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.CategoriaRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.CodigoAccesoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.EstadoOcRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.EstadoRespaldoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.LogAuditoriaRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.LoteProductoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.MovimientoInventarioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.NegocioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.ProductoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.ProveedorRepositoriImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.RolRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.SecuenciaCodigoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.TipoMovimientoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.UsuarioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICodigoAccesoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoOcJpaMapper;
// --- Imports de Mappers y JPA Repositorios ---
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoRespaldoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILogAuditoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILoteProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IMovimientoInventarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.INegocioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProveedorJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IRolJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ISecuenciaCodigoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITipoMovimientoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICategoriaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICodigoAccesoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoOcJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoRespaldoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILogAuditoriaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IMovimientoInventarioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.INegocioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProveedorJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IRolJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ISecuenciaCodigoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoMovimientoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IUsuarioJpaRepositorio;

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
	
	// ==================== NEGOCIO ====================
	@Bean
	INegocioRepositorio negocioRepositorio(INegocioJpaRepositorio jpaRepositorio, INegocioJpaMapper mapper) {
		return new NegocioRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	INegocioUseCase negocioUseCase(INegocioRepositorio repoUseCase) {
		return new NegocioUseCaseImpl(repoUseCase);
	}
	
	// ==================== ROL ====================
	@Bean
	IRolRepositorio rolRepositorio(IRolJpaRepositorio jpaRepositorio, IRolJpaMapper mapper) {
		return new RolRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IRolUseCase rolUseCase(IRolRepositorio repoUseCase) {
		return new RolUseCaseImpl(repoUseCase);
	}
	
	// ==================== USUARIO ====================
	@Bean
	IUsuarioRepositorio usuarioRepositorio(IUsuarioJpaRepositorio jpaRepositorio, IUsuarioJpaMapper mapper) {
		return new UsuarioRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	IUsuarioUseCase usuarioUseCase(IUsuarioRepositorio repoUseCase) {
		return new UsuarioUseCaseImpl(repoUseCase);
	}
	
	// ==================== CODIGO ACCESO ====================
	@Bean
	ICodigoAccesoRepositorio codigoAccesoRepositorio(ICodigoAccesoJpaRepositorio jpaRepositorio, ICodigoAccesoJpaMapper mapper) {
		return new CodigoAccesoRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	ICodigoAccesoUseCase codigoAccesoUseCase(ICodigoAccesoRepositorio repoUseCase) {
		return new CodigoAccesoUseCaseImpl(repoUseCase);
	}
	
	// ==================== LOG AUDITORIA ====================
	@Bean
	ILogAuditoriaRepositorio logAuditoriaRepositorio(ILogAuditoriaJpaRepositorio jpaRepositorio, ILogAuditoriaJpaMapper mapper) {
		return new LogAuditoriaRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	ILogAuditoriaUseCase logAuditoriaUseCase(ILogAuditoriaRepositorio repoUseCase) {
		return new LogAuditoriaUseCaseImpl(repoUseCase);
	}
	
	// ==================== ESTADO OC ====================
	@Bean
	IEstadoOcRepositorio estadoOcRepositorio(IEstadoOcJpaRepositorio jpaRepositorio, IEstadoOcJpaMapper mapper) {
		return new EstadoOcRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	IEstadoOcUseCase estadoOcUseCase(IEstadoOcRepositorio repoUseCase) {
		return new EstadoOcUseCaseImpl(repoUseCase);
	}
	
	// ==================== CATEGORIA ====================
	@Bean
	ICategoriaRepositorio categoriaRepositorio(ICategoriaJpaRepositorio jpaRepositorio, ICategoriaJpaMapper mapper) {
		return new CategoriaRepositorioImpl(jpaRepositorio, mapper);
	}
	
	@Bean
	ICategoriaUseCase categoriaUseCase(ICategoriaRepositorio repoUseCase) {
		return new CategoriaUseCaseImpl(repoUseCase);
	}
}