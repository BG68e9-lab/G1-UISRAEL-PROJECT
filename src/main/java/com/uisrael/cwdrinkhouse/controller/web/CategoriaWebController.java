package com.uisrael.cwdrinkhouse.controller.web;

import com.uisrael.cwdrinkhouse.controller.BaseController;
import com.uisrael.cwdrinkhouse.dto.CategoryDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Web MVC para gestión de categorías.
 * Consume el API REST usando WebClient y devuelve vistas Thymeleaf.
 * 
 * Este controlador maneja las rutas web para:
 * - Listar categorías
 * - Crear nuevas categorías
 * - Editar categorías existentes
 * - Eliminar categorías
 * 
 * Requiere autenticación de administrador (ADMIN) para todas las operaciones.
 * 
 * Valida: Requisitos RF-005
 */
@Controller
@RequestMapping("/categorias")
public class CategoriaWebController extends BaseController {

    private static final String CATEGORIAS_LIST_VIEW = "categorias/list";
    private static final String CATEGORIAS_FORM_VIEW = "categorias/form";
    private static final String REDIRECT_CATEGORIAS = "redirect:/categorias";

    @Autowired
    private WebClient webClient;

    /**
     * Listar todas las categorías.
     * GET /categorias
     * 
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @return vista de lista de categorías
     */
    @GetMapping
    public String listarCategorias(HttpSession session, Model model) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        // Verificar rol ADMIN
        if (!hasAdminRole(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        try {
            // Obtener categorías desde API REST
            List<CategoryDTO> categorias = obtenerCategorias();
            
            // Agregar atributos al modelo
            model.addAttribute("categorias", categorias);
            
            logger.info("Listando {} categorías", categorias.size());
            
            return CATEGORIAS_LIST_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener categorías desde API: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al cargar las categorías: " + obtenerMensajeError(e));
            model.addAttribute("categorias", new ArrayList<>());
            return CATEGORIAS_LIST_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al listar categorías", e);
            model.addAttribute("errorMessage", "Error inesperado al cargar las categorías");
            model.addAttribute("categorias", new ArrayList<>());
            return CATEGORIAS_LIST_VIEW;
        }
    }

    /**
     * Mostrar formulario de creación de categoría.
     * GET /categorias/new
     * 
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @return vista de formulario
     */
    @GetMapping("/new")
    public String mostrarFormularioCreacion(HttpSession session, Model model) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        // Verificar rol ADMIN
        if (!hasAdminRole(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        try {
            // Crear categoría vacía
            CategoryDTO categoria = new CategoryDTO();
            
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Categoría");
            
            logger.info("Mostrando formulario de creación de categoría");
            
            return CATEGORIAS_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error al cargar formulario de creación", e);
            return REDIRECT_CATEGORIAS + "?error=load_form_failed";
        }
    }

    /**
     * Procesar creación de categoría.
     * POST /categorias/new
     * 
     * @param categoria datos de la categoría
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/new")
    public String crearCategoria(
            @ModelAttribute CategoryDTO categoria,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        // Verificar rol ADMIN
        if (!hasAdminRole(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        try {
            // Crear categoría vía API REST
            CategoryDTO categoriaCreada = webClient.post()
                .uri("/categorias")
                .bodyValue(categoria)
                .retrieve()
                .bodyToMono(CategoryDTO.class)
                .block();
            
            logger.info("Categoría creada exitosamente: ID={}, Nombre={}", 
                categoriaCreada.getCategoriaId(), categoriaCreada.getNombre());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Categoría '" + categoriaCreada.getNombre() + "' creada exitosamente");
            
            return REDIRECT_CATEGORIAS;
            
        } catch (WebClientResponseException.Conflict e) {
            // 409 Conflict - ya existe
            logger.warn("Conflicto al crear categoría: ya existe");
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Categoría");
            model.addAttribute("errorMessage", "Ya existe una categoría con ese nombre");
            return CATEGORIAS_FORM_VIEW;
            
        } catch (WebClientResponseException.BadRequest e) {
            // 400 Bad Request - datos inválidos
            logger.warn("Datos inválidos al crear categoría: {}", e.getMessage());
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Categoría");
            model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
            return CATEGORIAS_FORM_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al crear categoría: {}", e.getMessage());
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Categoría");
            model.addAttribute("errorMessage", "Error al crear categoría: " + obtenerMensajeError(e));
            return CATEGORIAS_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al crear categoría", e);
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Categoría");
            model.addAttribute("errorMessage", "Error inesperado al crear categoría");
            return CATEGORIAS_FORM_VIEW;
        }
    }

    /**
     * Mostrar formulario de edición de categoría.
     * GET /categorias/{id}/edit
     * 
     * @param id ID de la categoría
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return vista de formulario
     */
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEdicion(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        // Verificar rol ADMIN
        if (!hasAdminRole(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        try {
            // Obtener categoría desde API REST
            CategoryDTO categoria = webClient.get()
                .uri("/categorias/{id}", id)
                .retrieve()
                .bodyToMono(CategoryDTO.class)
                .block();
            
            // Si el categoriaId no viene en la respuesta, asignarlo
            if (categoria.getCategoriaId() == null) {
                categoria.setCategoriaId(id);
            }
            
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Categoría");
            
            logger.info("Mostrando formulario de edición para categoría ID={}", id);
            
            return CATEGORIAS_FORM_VIEW;
            
        } catch (WebClientResponseException.NotFound e) {
            // 404 Not Found
            logger.warn("Categoría no encontrada: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Categoría no encontrada");
            return REDIRECT_CATEGORIAS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener categoría para edición: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al cargar categoría: " + obtenerMensajeError(e));
            return REDIRECT_CATEGORIAS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al cargar categoría para edición", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al cargar categoría");
            return REDIRECT_CATEGORIAS;
        }
    }

    /**
     * Procesar edición de categoría.
     * POST /categorias/{id}/edit
     * 
     * @param id ID de la categoría
     * @param categoria datos de la categoría
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/edit")
    public String editarCategoria(
            @PathVariable Long id,
            @ModelAttribute CategoryDTO categoria,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        // Verificar rol ADMIN
        if (!hasAdminRole(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        try {
            // Asegurar que el ID está establecido
            categoria.setCategoriaId(id);
            
            // Actualizar categoría vía API REST
            CategoryDTO categoriaActualizada = webClient.put()
                .uri("/categorias/{id}", id)
                .bodyValue(categoria)
                .retrieve()
                .bodyToMono(CategoryDTO.class)
                .block();
            
            logger.info("Categoría actualizada exitosamente: ID={}, Nombre={}", 
                id, categoriaActualizada.getNombre());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Categoría '" + categoriaActualizada.getNombre() + "' actualizada exitosamente");
            
            return REDIRECT_CATEGORIAS;
            
        } catch (WebClientResponseException.NotFound e) {
            // 404 Not Found
            logger.warn("Categoría no encontrada para actualización: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Categoría no encontrada");
            return REDIRECT_CATEGORIAS;
            
        } catch (WebClientResponseException.Conflict e) {
            // 409 Conflict - ya existe otra con ese nombre
            logger.warn("Conflicto al actualizar categoría: ID={}", id);
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Categoría");
            model.addAttribute("errorMessage", "Ya existe otra categoría con ese nombre");
            return CATEGORIAS_FORM_VIEW;
            
        } catch (WebClientResponseException.BadRequest e) {
            // 400 Bad Request - datos inválidos
            logger.warn("Datos inválidos al actualizar categoría: ID={}", id);
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Categoría");
            model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
            return CATEGORIAS_FORM_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al actualizar categoría: ID={}, Status={}", id, e.getStatusCode());
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Categoría");
            model.addAttribute("errorMessage", "Error al actualizar categoría: " + obtenerMensajeError(e));
            return CATEGORIAS_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar categoría: ID={}", id, e);
            model.addAttribute("categoria", categoria);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Categoría");
            model.addAttribute("errorMessage", "Error inesperado al actualizar categoría");
            return CATEGORIAS_FORM_VIEW;
        }
    }

    /**
     * Eliminar categoría.
     * POST /categorias/{id}/delete
     * 
     * @param id ID de la categoría
     * @param session sesión HTTP
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/delete")
    public String eliminarCategoria(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        // Verificar rol ADMIN
        if (!hasAdminRole(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        try {
            // Primero obtener el nombre de la categoría para el mensaje
            CategoryDTO categoria = webClient.get()
                .uri("/categorias/{id}", id)
                .retrieve()
                .bodyToMono(CategoryDTO.class)
                .block();
            
            String nombreCategoria = categoria != null ? categoria.getNombre() : "ID: " + id;
            
            // Eliminar categoría vía API REST
            webClient.delete()
                .uri("/categorias/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            
            logger.info("Categoría eliminada exitosamente: ID={}, Nombre={}", id, nombreCategoria);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Categoría '" + nombreCategoria + "' eliminada exitosamente");
            
            return REDIRECT_CATEGORIAS;
            
        } catch (WebClientResponseException.NotFound e) {
            // 404 Not Found
            logger.warn("Categoría no encontrada para eliminación: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Categoría no encontrada");
            return REDIRECT_CATEGORIAS;
            
        } catch (WebClientResponseException.UnprocessableEntity e) {
            // 422 Unprocessable Entity - tiene productos asociados
            logger.warn("No se puede eliminar categoría con productos asociados: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "No se puede eliminar la categoría porque tiene productos asociados");
            return REDIRECT_CATEGORIAS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al eliminar categoría: ID={}, Status={}", id, e.getStatusCode());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al eliminar categoría: " + obtenerMensajeError(e));
            return REDIRECT_CATEGORIAS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar categoría: ID={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al eliminar categoría");
            return REDIRECT_CATEGORIAS;
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtiene todas las categorías desde el API REST.
     * 
     * @return lista de categorías
     */
    private List<CategoryDTO> obtenerCategorias() {
        try {
            return webClient.get()
                .uri("/categorias")
                .retrieve()
                .bodyToFlux(CategoryDTO.class)
                .collectList()
                .block();
                
        } catch (Exception e) {
            logger.error("Error al obtener categorías desde API", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene mensaje de error amigable desde WebClientResponseException.
     * 
     * @param e excepción de WebClient
     * @return mensaje de error amigable
     */
    private String obtenerMensajeError(WebClientResponseException e) {
        return switch (e.getStatusCode().value()) {
            case 400 -> "Datos inválidos";
            case 404 -> "Recurso no encontrado";
            case 409 -> "Conflicto: el recurso ya existe";
            case 422 -> "No se puede eliminar porque tiene dependencias";
            case 500 -> "Error interno del servidor";
            default -> "Error inesperado (código " + e.getStatusCode().value() + ")";
        };
    }
}
