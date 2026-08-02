package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CodigoAccesoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICodigoAccesoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICodigoAccesoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IUsuarioJpaRepositorio;

/**
 * El dominio CodigoAcceso es "plano" (solo usuarioId), por lo que la
 * resolucion de la relacion con Usuario vive en este adaptador, igual que en
 * LoteProductoRepositorioImpl.
 */
public class CodigoAccesoRepositorioImpl implements ICodigoAccesoRepositorio {

	private final ICodigoAccesoJpaRepositorio jpaRepositorio;
	private final ICodigoAccesoJpaMapper codigoAccesoMapper;
	private final IUsuarioJpaRepositorio usuarioJpaRepositorio;

	public CodigoAccesoRepositorioImpl(ICodigoAccesoJpaRepositorio jpaRepositorio,
			ICodigoAccesoJpaMapper codigoAccesoMapper, IUsuarioJpaRepositorio usuarioJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.codigoAccesoMapper = codigoAccesoMapper;
		this.usuarioJpaRepositorio = usuarioJpaRepositorio;
	}

	@Override
	public CodigoAcceso guardar(CodigoAcceso nuevoCodigoAcceso) {
		CodigoAccesoEntity entity = codigoAccesoMapper.toEntity(nuevoCodigoAcceso);

		if (nuevoCodigoAcceso.getUsuarioId() != null) {
			UsuarioEntity usuario = usuarioJpaRepositorio.findById(nuevoCodigoAcceso.getUsuarioId())
					.orElseThrow(() -> new IllegalArgumentException(
							"El usuario indicado no existe: " + nuevoCodigoAcceso.getUsuarioId()));
			entity.setFkUsuarioEntity(usuario);
		}

		CodigoAccesoEntity guardado = jpaRepositorio.save(entity);
		return toDomainConUsuario(guardado);
	}

	@Override
	public Optional<CodigoAcceso> buscarPorId(UUID idCodigoAcceso) {
		return jpaRepositorio.findById(idCodigoAcceso).map(this::toDomainConUsuario);
	}

	@Override
	public List<CodigoAcceso> listarTodos() {
		return jpaRepositorio.findAll().stream().map(this::toDomainConUsuario).toList();
	}

	@Override
	public void eliminar(UUID idCodigoAcceso) {
		jpaRepositorio.deleteById(idCodigoAcceso);
	}

	@Override
	public List<CodigoAcceso> buscarVigentesPorUsuarioYTipo(UUID idUsuario, String tipoCodigo) {
		return jpaRepositorio
				.findByFkUsuarioEntity_UsuarioIdAndTipoCodigoAndUsadoFalseOrderByCreadoEnDesc(idUsuario, tipoCodigo)
				.stream().map(this::toDomainConUsuario).toList();
	}

	private CodigoAcceso toDomainConUsuario(CodigoAccesoEntity entity) {
		CodigoAcceso dominio = codigoAccesoMapper.toDomain(entity);
		if (entity.getFkUsuarioEntity() != null) {
			dominio.setUsuarioId(entity.getFkUsuarioEntity().getUsuarioId());
		}
		return dominio;
	}

}
