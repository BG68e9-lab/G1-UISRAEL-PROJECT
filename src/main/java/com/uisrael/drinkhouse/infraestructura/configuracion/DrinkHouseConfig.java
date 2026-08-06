package com.uisrael.drinkhouse.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAlertaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICategoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoOcUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoRespaldoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IIdentificacionIaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INegocioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INotaVentaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IOrdenCompraUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRolUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoMovimientoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IUsuarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.AlertaUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.CategoriaUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.CodigoAccesoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.EstadoOcUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.EstadoRespaldoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.IdentificacionIaUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.LogAuditoriaUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.LoteProductoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.MovimientoInventarioUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.NegocioUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.NotaVentaUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.OrdenCompraUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.ProductoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.ProveedorUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.RolUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.SecuenciaCodigoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.TipoMovimientoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.TipoProductoUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.casosuso.impl.UsuarioUseCaseImpl;
import com.uisrael.drinkhouse.aplicacion.servicios.StockValidator;
import com.uisrael.drinkhouse.dominio.repositorios.IAjusteInventarioAuditoriaRepositorio;
import com.uisrael.drinkhouse.infraestructura.servicios.EmailService;
import com.uisrael.drinkhouse.dominio.repositorios.IAlertaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ICategoriaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IConsumoIaMensualRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IDetalleOrdenCompraRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoOcRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoRespaldoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IIdentificacionIaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ILogAuditoriaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.INegocioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.INotaVentaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IOrdenCompraRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProveedorRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IRolRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ISecuenciaCodigoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITokensIaNegocioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.AlertaRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.CategoriaRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.CodigoAccesoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.ConsumoIaMensualRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.DetalleOrdenCompraRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.EstadoOcRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.EstadoRespaldoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.IdentificacionIaRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.LogAuditoriaRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.LoteProductoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.MovimientoInventarioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.NegocioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.OrdenCompraRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.ProductoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.ProveedorRepositoriImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.RolRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.SecuenciaCodigoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.TipoMovimientoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.TipoProductoRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.TokensIaNegocioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores.UsuarioRepositorioImpl;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IAlertaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICodigoAccesoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IConsumoIaMensualJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IDetalleOrdenCompraJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoOcJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoRespaldoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IIdentificacionIaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILogAuditoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILoteProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IMovimientoInventarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.INegocioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IOrdenCompraJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProveedorJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IRolJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ISecuenciaCodigoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITipoMovimientoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITipoProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITokensIaNegocioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IAlertaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICategoriaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICodigoAccesoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IConsumoIaMensualJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IDetalleOrdenCompraJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoOcJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoRespaldoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IIdentificacionIaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILogAuditoriaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IMovimientoInventarioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.INegocioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IOrdenCompraJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProveedorJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IRolJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ISecuenciaCodigoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoMovimientoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITokensIaNegocioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IUsuarioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.servicios.ClaudeVisionService;

@Configuration
@EnableScheduling
public class DrinkHouseConfig {

	@Bean
	IAlertaRepositorio alertaRepositorio(IAlertaJpaRepositorio jpaRepositorio, IAlertaJpaMapper mapper) {
	    return new AlertaRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IAlertaUseCase alertaUseCase(IAlertaRepositorio repoUseCase) {
	    return new AlertaUseCaseImpl(repoUseCase);
	}

	@Bean
	IProductoRepositorio productoRepositorio(IProductoJpaRepositorio jpaRepositorio, IProductoJpaMapper mapper) {
		return new ProductoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IProductoUseCase productoUseCase(IProductoRepositorio repoUseCase, ILogAuditoriaUseCase logAuditoriaUseCase) {
		return new ProductoUseCaseImpl(repoUseCase, logAuditoriaUseCase);
	}

	@Bean
	IProveedorRepositorio proveedorRepositorio(IProveedorJpaRepositorio jpaRepositorio, IProveedorJpaMapper mapper) {
		return new ProveedorRepositoriImpl(jpaRepositorio, mapper);
	}

	@Bean
	IProveedorUseCase proveedorUseCase(IProveedorRepositorio repoUseCase, ILogAuditoriaUseCase logAuditoriaUseCase) {
		return new ProveedorUseCaseImpl(repoUseCase, logAuditoriaUseCase);
	}
	
	@Bean
	com.uisrael.drinkhouse.aplicacion.servicios.FacturaIAService facturaIAService(IProveedorUseCase proveedorUseCase) {
		return new com.uisrael.drinkhouse.aplicacion.servicios.FacturaIAService(proveedorUseCase);
	}

	@Bean
	ILoteProductoRepositorio loteProductoRepositorio(ILoteProductoJpaRepositorio jpaRepositorio,
			ILoteProductoJpaMapper mapper, jakarta.persistence.EntityManager entityManager) {
		return new LoteProductoRepositorioImpl(jpaRepositorio, mapper, entityManager);
	}

	@Bean
	ILoteProductoUseCase loteProductoUseCase(ILoteProductoRepositorio repoUseCase,
			IProductoRepositorio productoRepositorio, ISecuenciaCodigoUseCase secuenciaCodigoUseCase,
			ITipoMovimientoRepositorio tipoMovimientoRepositorio) {
		return new LoteProductoUseCaseImpl(repoUseCase, productoRepositorio, secuenciaCodigoUseCase,
				tipoMovimientoRepositorio);
	}

	@Bean
	IMovimientoInventarioRepositorio movimientoInventarioRepositorio(IMovimientoInventarioJpaRepositorio jpaRepositorio,
			IMovimientoInventarioJpaMapper mapper, IProductoJpaRepositorio productoJpaRepositorio,
			ILoteProductoJpaRepositorio loteJpaRepositorio,
			ITipoMovimientoJpaRepositorio tipoMovimientoJpaRepositorio) {
		return new MovimientoInventarioRepositorioImpl(jpaRepositorio, mapper, productoJpaRepositorio,
				loteJpaRepositorio, tipoMovimientoJpaRepositorio);
	}

	@Bean
	IMovimientoInventarioUseCase movimientoInventarioUseCase(IMovimientoInventarioRepositorio repoUseCase,
			IProductoRepositorio productoRepositorio, ILoteProductoRepositorio loteProductoRepositorio,
			ITipoMovimientoRepositorio tipoMovimientoRepositorio, ISecuenciaCodigoUseCase secuenciaCodigoUseCase,
			IAlertaUseCase alertaUseCase, ILogAuditoriaUseCase logAuditoriaUseCase,
			IAjusteInventarioAuditoriaRepositorio ajusteAuditoriaRepositorio,
			StockValidator stockValidator, ICodigoAccesoUseCase codigoAccesoUseCase,
			com.uisrael.drinkhouse.dominio.repositorios.INotaVentaRepositorio notaVentaRepositorio,
			com.uisrael.drinkhouse.dominio.repositorios.IVentaRepositorio ventaRepositorio) {
		return new MovimientoInventarioUseCaseImpl(repoUseCase, productoRepositorio, loteProductoRepositorio,
				tipoMovimientoRepositorio, secuenciaCodigoUseCase, alertaUseCase, logAuditoriaUseCase,
				ajusteAuditoriaRepositorio, stockValidator, codigoAccesoUseCase,
				notaVentaRepositorio, ventaRepositorio);
	}

	@Bean
	IEstadoRespaldoRepositorio estadoRespaldoRepositorio(IEstadoRespaldoJpaRepositorio jpaRepositorio,
			IEstadoRespaldoJpaMapper mapper) {
		return new EstadoRespaldoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IEstadoRespaldoUseCase estadoRespaldoUseCase(IEstadoRespaldoRepositorio repoUseCase) {
		return new EstadoRespaldoUseCaseImpl(repoUseCase);
	}

	@Bean
	ISecuenciaCodigoRepositorio secuenciaCodigoRepositorio(ISecuenciaCodigoJpaRepositorio jpaRepositorio,
			ISecuenciaCodigoJpaMapper mapper) {
		return new SecuenciaCodigoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ISecuenciaCodigoUseCase secuenciaCodigoUseCase(ISecuenciaCodigoRepositorio repoUseCase) {
		return new SecuenciaCodigoUseCaseImpl(repoUseCase);
	}

	@Bean
	ITipoMovimientoRepositorio tipoMovimientoRepositorio(ITipoMovimientoJpaRepositorio jpaRepositorio,
			ITipoMovimientoJpaMapper mapper) {
		return new TipoMovimientoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ITipoMovimientoUseCase tipoMovimientoUseCase(ITipoMovimientoRepositorio repoUseCase) {
		return new TipoMovimientoUseCaseImpl(repoUseCase);
	}

	@Bean
	INegocioRepositorio negocioRepositorio(INegocioJpaRepositorio jpaRepositorio, INegocioJpaMapper mapper) {
		return new NegocioRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	INegocioUseCase negocioUseCase(INegocioRepositorio repoUseCase, ILogAuditoriaUseCase logAuditoriaUseCase) {
		return new NegocioUseCaseImpl(repoUseCase, logAuditoriaUseCase);
	}

	@Bean
	IRolRepositorio rolRepositorio(IRolJpaRepositorio jpaRepositorio, IRolJpaMapper mapper) {
		return new RolRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IRolUseCase rolUseCase(IRolRepositorio repoUseCase, IUsuarioRepositorio usuarioRepositorio) {
		return new RolUseCaseImpl(repoUseCase, usuarioRepositorio);
	}

	@Bean
	IUsuarioRepositorio usuarioRepositorio(IUsuarioJpaRepositorio jpaRepositorio, IUsuarioJpaMapper mapper,
			IRolJpaRepositorio rolJpaRepositorio) {
		return new UsuarioRepositorioImpl(jpaRepositorio, mapper, rolJpaRepositorio);
	}

	@Bean
	IUsuarioUseCase usuarioUseCase(IUsuarioRepositorio repoUseCase, ILogAuditoriaUseCase logAuditoriaUseCase) {
		return new UsuarioUseCaseImpl(repoUseCase, logAuditoriaUseCase);
	}

	@Bean
	ICodigoAccesoRepositorio codigoAccesoRepositorio(ICodigoAccesoJpaRepositorio jpaRepositorio,
			ICodigoAccesoJpaMapper mapper) {
		return new CodigoAccesoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ICodigoAccesoUseCase codigoAccesoUseCase(ICodigoAccesoRepositorio repoUseCase,
			ILogAuditoriaUseCase logAuditoriaUseCase, EmailService emailService) {
		return new CodigoAccesoUseCaseImpl(repoUseCase, logAuditoriaUseCase, emailService);
	}

	@Bean
	ILogAuditoriaRepositorio logAuditoriaRepositorio(ILogAuditoriaJpaRepositorio jpaRepositorio,
			ILogAuditoriaJpaMapper mapper) {
		return new LogAuditoriaRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ILogAuditoriaUseCase logAuditoriaUseCase(ILogAuditoriaRepositorio repoUseCase, ObjectMapper objectMapper) {
		return new LogAuditoriaUseCaseImpl(repoUseCase, objectMapper);
	}

	@Bean
	IEstadoOcRepositorio estadoOcRepositorio(IEstadoOcJpaRepositorio jpaRepositorio, IEstadoOcJpaMapper mapper) {
		return new EstadoOcRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IEstadoOcUseCase estadoOcUseCase(IEstadoOcRepositorio repoUseCase) {
		return new EstadoOcUseCaseImpl(repoUseCase);
	}

	@Bean
	ICategoriaRepositorio categoriaRepositorio(ICategoriaJpaRepositorio jpaRepositorio, ICategoriaJpaMapper mapper) {
		return new CategoriaRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ICategoriaUseCase categoriaUseCase(ICategoriaRepositorio repoUseCase, ILogAuditoriaUseCase logAuditoriaUseCase) {
		return new CategoriaUseCaseImpl(repoUseCase, logAuditoriaUseCase);
	}

	@Bean
	ITipoProductoRepositorio tipoProductoRepositorio(ITipoProductoJpaRepositorio jpaRepositorio, 
			ITipoProductoJpaMapper mapper) {
		return new TipoProductoRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ITipoProductoUseCase tipoProductoUseCase(ITipoProductoRepositorio tipoProductoRepositorio,
			ICategoriaRepositorio categoriaRepositorio, ILogAuditoriaUseCase logAuditoriaUseCase) {
		return new TipoProductoUseCaseImpl(tipoProductoRepositorio, categoriaRepositorio, logAuditoriaUseCase);
	}

	@Bean
	IIdentificacionIaRepositorio identificacionIaRepositorio(IIdentificacionIaJpaRepositorio jpaRepositorio,
			IIdentificacionIaJpaMapper mapper) {
		return new IdentificacionIaRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IIdentificacionIaUseCase identificacionIaUseCase(IIdentificacionIaRepositorio identificacionRepositorio,
			IConsumoIaMensualRepositorio consumoRepositorio, ITokensIaNegocioRepositorio tokensRepositorio,
			IProductoRepositorio productoRepositorio, ClaudeVisionService claudeVisionService) {
		return new IdentificacionIaUseCaseImpl(identificacionRepositorio, consumoRepositorio, tokensRepositorio,
				productoRepositorio, claudeVisionService);
	}

	@Bean
	IConsumoIaMensualRepositorio consumoIaMensualRepositorio(IConsumoIaMensualJpaRepositorio jpaRepositorio,
			IConsumoIaMensualJpaMapper mapper) {
		return new ConsumoIaMensualRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ITokensIaNegocioRepositorio tokensIaNegocioRepositorio(ITokensIaNegocioJpaRepositorio jpaRepositorio,
			ITokensIaNegocioJpaMapper mapper) {
		return new TokensIaNegocioRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IOrdenCompraRepositorio ordenCompraRepositorio(IOrdenCompraJpaRepositorio jpaRepositorio,
			IOrdenCompraJpaMapper mapper, IProveedorJpaRepositorio proveedorJpaRepositorio,
			IEstadoOcJpaRepositorio estadoOcJpaRepositorio) {
		return new OrdenCompraRepositorioImpl(jpaRepositorio, mapper, proveedorJpaRepositorio, estadoOcJpaRepositorio);
	}

	@Bean
	IOrdenCompraUseCase ordenCompraUseCase(IOrdenCompraRepositorio ordenCompraRepositorio,
			IDetalleOrdenCompraRepositorio detalleRepositorio, IProductoRepositorio productoRepositorio,
			ILoteProductoRepositorio loteRepositorio, ISecuenciaCodigoUseCase secuenciaUseCase,
			ILogAuditoriaUseCase logAuditoriaUseCase, ITipoMovimientoRepositorio tipoMovimientoRepositorio,
			INegocioRepositorio negocioRepositorio, IMovimientoInventarioUseCase movimientoInventarioUseCase) {
		return new OrdenCompraUseCaseImpl(ordenCompraRepositorio, detalleRepositorio, productoRepositorio,
				loteRepositorio, secuenciaUseCase, logAuditoriaUseCase, tipoMovimientoRepositorio,
				negocioRepositorio, movimientoInventarioUseCase);
	}

	@Bean
	IDetalleOrdenCompraRepositorio detalleOrdenCompraRepositorio(IDetalleOrdenCompraJpaRepositorio jpaRepositorio,
			IDetalleOrdenCompraJpaMapper mapper) {
		return new DetalleOrdenCompraRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	INotaVentaUseCase notaVentaUseCase(INotaVentaRepositorio notaVentaRepositorio) {
		return new NotaVentaUseCaseImpl(notaVentaRepositorio);
	}

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}