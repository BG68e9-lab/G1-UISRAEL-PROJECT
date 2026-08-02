package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TasaIva;
import com.uisrael.drinkhouse.dominio.repositorios.ITasaIvaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TasaIvaEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITasaIvaJpaRepositorio;

public class TasaIvaRepositorioImpl implements ITasaIvaRepositorio {

	private final ITasaIvaJpaRepositorio jpaRepositorio;

	public TasaIvaRepositorioImpl(ITasaIvaJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public TasaIva registrarNuevaTasa(BigDecimal porcentaje, String motivo) {
		OffsetDateTime ahora = OffsetDateTime.now();

		jpaRepositorio.findFirstByVigenteHastaIsNullOrderByVigenteDesdeDesc().ifPresent(vigente -> {
			vigente.setVigenteHasta(ahora);
			jpaRepositorio.save(vigente);
		});

		TasaIvaEntity nueva = new TasaIvaEntity();
		nueva.setPorcentaje(porcentaje);
		nueva.setVigenteDesde(ahora);
		nueva.setMotivo(motivo);

		return toDomain(jpaRepositorio.save(nueva));
	}

	@Override
	public Optional<TasaIva> obtenerVigente() {
		return jpaRepositorio.findFirstByVigenteHastaIsNullOrderByVigenteDesdeDesc().map(this::toDomain);
	}

	@Override
	public List<TasaIva> listarHistorial() {
		return jpaRepositorio.findAllByOrderByVigenteDesdeDesc().stream().map(this::toDomain).toList();
	}

	private TasaIva toDomain(TasaIvaEntity entity) {
		TasaIva tasaIva = new TasaIva();
		tasaIva.setTasaIvaId(entity.getTasaIvaId());
		tasaIva.setPorcentaje(entity.getPorcentaje());
		tasaIva.setVigenteDesde(entity.getVigenteDesde());
		tasaIva.setVigenteHasta(entity.getVigenteHasta());
		tasaIva.setMotivo(entity.getMotivo());
		tasaIva.setCreadoEn(entity.getCreadoEn());
		return tasaIva;
	}
}
