package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.IProveedorRepositorio;

public class ProveedorUseCaseImpl implements IProveedorUseCase {

	private final IProveedorRepositorio repositorio;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;

	public ProveedorUseCaseImpl(IProveedorRepositorio repositorio, ILogAuditoriaUseCase logAuditoriaUseCase) {
		this.repositorio = repositorio;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
	}

	@Override
	public Proveedor crearProveedor(Proveedor proveedor) {
		validarRuc(proveedor.getRuc());
		if (repositorio.existePorRuc(proveedor.getRuc())) {
			throw new ConflictoUnicoException("Ya existe un proveedor con RUC: " + proveedor.getRuc());
		}
		Proveedor guardado = repositorio.guardar(proveedor);
		logAuditoriaUseCase.registrar("Proveedor", guardado.getProveedorId().toString(), "CREAR", guardado);
		return guardado;
	}

	@Override
	public Proveedor actualizarProveedor(Long id, Proveedor proveedor) {
		Proveedor existente = repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
		existente.setRazonSocial(proveedor.getRazonSocial());
		existente.setDireccion(proveedor.getDireccion());
		existente.setTelefono(proveedor.getTelefono());
		existente.setEmail(proveedor.getEmail());
		Proveedor actualizado = repositorio.guardar(existente);
		logAuditoriaUseCase.registrar("Proveedor", id.toString(), "ACTUALIZAR", actualizado);
		return actualizado;
	}

	@Override
	public Proveedor buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
	}

	@Override
	public List<Proveedor> listarProveedores() {
		return repositorio.listarTodos();
	}
	
	@Override
	public Proveedor buscarPorRuc(String ruc) {
		return repositorio.buscarPorRuc(ruc)
				.orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con RUC: " + ruc));
	}
	
	@Override
	public Proveedor buscarOCrearPorRuc(Proveedor proveedorNuevo) {
		validarRuc(proveedorNuevo.getRuc());
		
		Optional<Proveedor> proveedorExistente = repositorio.buscarPorRuc(proveedorNuevo.getRuc());
		
		if (proveedorExistente.isPresent()) {
			return proveedorExistente.get();
		}
		
		validarDatosProveedor(proveedorNuevo);
		Proveedor guardado = repositorio.guardar(proveedorNuevo);
		logAuditoriaUseCase.registrar("Proveedor", guardado.getProveedorId().toString(), 
			"CREAR_AUTO", "Proveedor creado automáticamente desde factura: " + proveedorNuevo.getRazonSocial());
		return guardado;
	}

	@Override
	public void eliminarProveedor(Long id) {
		repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
		if (repositorio.tieneOrdenesAsociadas(id)) {
			throw new ReglaNegocioException("No se puede eliminar el proveedor porque tiene órdenes de compra asociadas");
		}
		repositorio.eliminar(id);
		logAuditoriaUseCase.registrar("Proveedor", id.toString(), "ELIMINAR", null);
	}

	private void validarRuc(String ruc) {
		if (ruc == null || !ruc.matches("\\d{13}")) {
			throw new ReglaNegocioException("RUC inválido: debe tener exactamente 13 dígitos numéricos");
		}
	}
	
	private void validarDatosProveedor(Proveedor proveedor) {
		if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
			throw new ReglaNegocioException("La razón social del proveedor es obligatoria");
		}
		if (proveedor.getEmail() == null || proveedor.getEmail().trim().isEmpty()) {
			throw new ReglaNegocioException("El email del proveedor es obligatorio");
		}
	}
}
