package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICategoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Categoria;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.ICategoriaRepositorio;

public class CategoriaUseCaseImpl implements ICategoriaUseCase {

    private final ICategoriaRepositorio repositorio;
    private final ILogAuditoriaUseCase logAuditoriaUseCase;

    public CategoriaUseCaseImpl(ICategoriaRepositorio repositorio, ILogAuditoriaUseCase logAuditoriaUseCase) {
        this.repositorio = repositorio;
        this.logAuditoriaUseCase = logAuditoriaUseCase;
    }

    @Override
    public Categoria crearCategoria(Categoria categoria) {
        if (repositorio.existePorNombre(categoria.getNombre())) {
            throw new ConflictoUnicoException("Ya existe una categoría con nombre: " + categoria.getNombre());
        }
        if (categoria.getActivo() == null) {
            categoria.setActivo(true);
        }
        Categoria guardado = repositorio.guardar(categoria);
        logAuditoriaUseCase.registrar("Categoria", guardado.getCategoriaId().toString(), "CREAR", guardado);
        return guardado;
    }

    @Override
    public Categoria actualizarCategoria(Long id, Categoria categoria) {
        Categoria existente = repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
        existente.setNombre(categoria.getNombre());
        existente.setMargenGananciaPct(categoria.getMargenGananciaPct());
        if (categoria.getActivo() != null) {
            existente.setActivo(categoria.getActivo());
        }
        Categoria actualizado = repositorio.guardar(existente);
        logAuditoriaUseCase.registrar("Categoria", id.toString(), "ACTUALIZAR", actualizado);
        return actualizado;
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repositorio.listarTodas();
    }

    @Override
    public void eliminarCategoria(Long id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
        if (repositorio.tieneProductosAsociados(id)) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        repositorio.eliminar(id);
        logAuditoriaUseCase.registrar("Categoria", id.toString(), "ELIMINAR", null);
    }
}
