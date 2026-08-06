package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;
import com.uisrael.drinkhouse.infraestructura.servicios.EmailService;

public class CodigoAccesoUseCaseImpl implements ICodigoAccesoUseCase {

	private static final Logger logger = LoggerFactory.getLogger(CodigoAccesoUseCaseImpl.class);

	private final ICodigoAccesoRepositorio repositorio;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;
	private final EmailService emailService;

	public CodigoAccesoUseCaseImpl(ICodigoAccesoRepositorio repositorio,
			ILogAuditoriaUseCase logAuditoriaUseCase,
			EmailService emailService) {
		this.repositorio = repositorio;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
		this.emailService = emailService;
	}

	@Override
	public CodigoAcceso generarCodigo(String tipoCodigo, UUID usuarioId) {
		String codigoAleatorio = String.format("%06d", (int) (Math.random() * 1000000));
		
		OffsetDateTime expiraEn;
		if ("MOV_INVENTARIO".equals(tipoCodigo) || "RECUPERACION_PASS".equals(tipoCodigo)) {
			expiraEn = OffsetDateTime.now().plusMinutes(10);
		} else {
			expiraEn = OffsetDateTime.now().plusHours(24);
		}
		
		CodigoAcceso codigo = new CodigoAcceso();
		codigo.setCodigoHash(codigoAleatorio);
		codigo.setTipoCodigo(tipoCodigo);
		codigo.setUsuarioId(usuarioId);
		codigo.setExpiraEn(expiraEn);
		codigo.setUsado(false);
		
		CodigoAcceso guardado = repositorio.guardar(codigo);
		
		logAuditoriaUseCase.registrar("CodigoAcceso", guardado.getCodigoAccesoId().toString(),
				"CREAR", "Código generado para usuario: " + usuarioId + ", tipo: " + tipoCodigo);
		
		try {
			emailService.enviarCodigoAcceso(usuarioId, codigoAleatorio, tipoCodigo);
			logger.info("Código {} tipo {} enviado por email al usuario {}", codigoAleatorio, tipoCodigo, usuarioId);
		} catch (Exception e) {
			logger.error("Error al enviar código por email: {}", e.getMessage());
		}
		
		return guardado;
	}

	@Override
	public CodigoAcceso validarCodigo(String codigoHash) {
		CodigoAcceso codigo = repositorio.buscarPorHash(codigoHash)
				.orElseThrow(() -> new  com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException("Código de acceso no encontrado"));

		if (Boolean.TRUE.equals(codigo.getUsado())) {
			throw new ReglaNegocioException("El código ya fue utilizado");
		}
		if (codigo.getExpiraEn() != null && codigo.getExpiraEn().isBefore(OffsetDateTime.now())) {
			throw new ReglaNegocioException("El código está vencido");
		}

		codigo.setUsado(true);
		codigo.setUsadoEn(OffsetDateTime.now());
		CodigoAcceso actualizado = repositorio.guardar(codigo);
		logAuditoriaUseCase.registrar("CodigoAcceso", actualizado.getCodigoAccesoId().toString(),
				"USAR", actualizado);
		return actualizado;
	}
	
	@Override
	public CodigoAcceso validarCodigoSinMarcar(String codigoHash) {
		CodigoAcceso codigo = repositorio.buscarPorHash(codigoHash)
				.orElseThrow(() -> new com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException("Código de acceso no encontrado"));

		if (Boolean.TRUE.equals(codigo.getUsado())) {
			throw new ReglaNegocioException("El código ya fue utilizado");
		}
		
		if (codigo.getExpiraEn() != null && codigo.getExpiraEn().isBefore(OffsetDateTime.now())) {
			throw new ReglaNegocioException("El código está vencido");
		}

		logAuditoriaUseCase.registrar("CodigoAcceso", codigo.getCodigoAccesoId().toString(),
				"VALIDAR_SIN_MARCAR", "Código validado sin marcar como usado");
		
		return codigo;
	}
	
	@Override
	public CodigoAcceso marcarCodigoComoUsado(String codigoHash) {
		CodigoAcceso codigo = repositorio.buscarPorHash(codigoHash)
				.orElseThrow(() -> new com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException("Código de acceso no encontrado"));

		codigo.setUsado(true);
		codigo.setUsadoEn(OffsetDateTime.now());
		
		CodigoAcceso actualizado = repositorio.guardar(codigo);
		logAuditoriaUseCase.registrar("CodigoAcceso", actualizado.getCodigoAccesoId().toString(),
				"MARCAR_USADO", "Código marcado como usado");
		
		return actualizado;
	}
	
	@Override
	public Optional<CodigoAcceso> buscarUltimoCodigoPorUsuarioYTipo(UUID usuarioId, String tipoCodigo) {
		return repositorio.buscarUltimoCodigoPorUsuarioYTipo(usuarioId, tipoCodigo);
	}
}
