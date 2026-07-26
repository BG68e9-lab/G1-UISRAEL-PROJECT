package com.uisrael.cwdrinkhouse.controller.web;

import com.uisrael.cwdrinkhouse.controller.BaseController;
import com.uisrael.cwdrinkhouse.dto.ProviderDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Web MVC para gestión de proveedores.
 * Consume el API REST usando WebClient y devuelve vistas Thymeleaf.
 * 
 * Este controlador maneja las rutas web para:
 * - Listar proveedores (solo ADMIN)
 * - Crear nuevos proveedores (solo ADMIN)
 * - Editar proveedores existentes (solo ADMIN)
 * - Eliminar proveedores (solo ADMIN)
 * 
 * Requiere autenticación de administrador para todas las operaciones.
 * 
 * Valida: Requisitos 4.7, 14.1-14.10
 */
@Controller
@RequestMapping("/proveedores")
public class ProveedorWebController extends BaseController {

    private static final String PROVEEDORES_LIST_VIEW = "proveedores/list";
    private static final String PROVEEDORES_FORM_VIEW = "proveedores/form";
    private static final String REDIRECT_PROVEEDORES = "redirect:/proveedores";

    @Autowired
    private WebClient webClient;

    /**
     * Listar proveedores.
     * GET /proveedores
     * Solo accesible para administradores.
     * 
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @return vista de lista de proveedores
     */
    @GetMapping
    public String listarProveedores(HttpSession session, Model model) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó acceder a proveedores");
            model.addAttribute("errorMessage", "No tienes permisos para acceder a esta sección");
            return "error/403";
        }

        try {
            // Obtener proveedores desde API REST
            List<ProviderDTO> proveedores = obtenerProveedores();
            
            model.addAttribute("proveedores", proveedores);
            
            logger.info("Listando {} proveedores", proveedores.size());
            
            return PROVEEDORES_LIST_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener proveedores desde API: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al cargar los proveedores: " + obtenerMensajeError(e));
            model.addAttribute("proveedores", new ArrayList<>());
            return PROVEEDORES_LIST_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al listar proveedores", e);
            model.addAttribute("errorMessage", "Error inesperado al cargar los proveedores");
            model.addAttribute("proveedores", new ArrayList<>());
            return PROVEEDORES_LIST_VIEW;
        }
    }

    /**
     * Mostrar formulario de creación de proveedor.
     * GET /proveedores/new
     * Solo accesible para administradores.
     * 
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @return vista de formulario
     */
    @GetMapping("/new")
    public String mostrarFormularioCreacion(HttpSession session, Model model) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó crear proveedor");
            return "redirect:/dashboard";
        }

        try {
            // Crear proveedor vacío con valores por defecto
            ProviderDTO proveedor = new ProviderDTO();
            proveedor.setActivo(true);
            
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Proveedor");
            
            logger.info("Mostrando formulario de creación de proveedor");
            
            return PROVEEDORES_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error al cargar formulario de creación", e);
            return REDIRECT_PROVEEDORES + "?error=load_form_failed";
        }
    }

    /**
     * Procesar creación de proveedor.
     * POST /proveedores/new
     * Solo accesible para administradores.
     * 
     * @param proveedor datos del proveedor
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/new")
    public String crearProveedor(
            @ModelAttribute ProviderDTO proveedor,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó crear proveedor");
            return "redirect:/dashboard";
        }

        try {
            // Crear proveedor vía API REST
            ProviderDTO proveedorCreado = webClient.post()
                .uri("/proveedores")
                .bodyValue(proveedor)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .block();
            
            logger.info("Proveedor creado exitosamente: ID={}, RUC={}, Razón Social={}", 
                proveedorCreado.getProveedorId(), proveedorCreado.getRuc(), proveedorCreado.getRazonSocial());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Proveedor '" + proveedorCreado.getRazonSocial() + "' creado exitosamente");
            
            return REDIRECT_PROVEEDORES;
            
        } catch (WebClientResponseException.Conflict e) {
            // 409 Conflict - ya existe (RUC duplicado)
            logger.warn("Conflicto al crear proveedor: RUC ya existe");
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Proveedor");
            model.addAttribute("errorMessage", "Ya existe un proveedor con ese RUC");
            return PROVEEDORES_FORM_VIEW;
            
        } catch (WebClientResponseException.BadRequest e) {
            // 400 Bad Request - datos inválidos
            logger.warn("Datos inválidos al crear proveedor: {}", e.getMessage());
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Proveedor");
            model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
            return PROVEEDORES_FORM_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al crear proveedor: {}", e.getMessage());
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Proveedor");
            model.addAttribute("errorMessage", "Error al crear proveedor: " + obtenerMensajeError(e));
            return PROVEEDORES_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al crear proveedor", e);
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Proveedor");
            model.addAttribute("errorMessage", "Error inesperado al crear proveedor");
            return PROVEEDORES_FORM_VIEW;
        }
    }

    /**
     * Mostrar formulario de edición de proveedor.
     * GET /proveedores/{id}/edit
     * Solo accesible para administradores.
     * 
     * @param id ID del proveedor
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
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó editar proveedor");
            return "redirect:/dashboard";
        }

        try {
            // Obtener proveedor desde API REST
            ProviderDTO proveedor = webClient.get()
                .uri("/proveedores/{id}", id)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .block();
            
            // Si el proveedorId no viene en la respuesta, asignarlo
            if (proveedor.getProveedorId() == null) {
                proveedor.setProveedorId(id);
            }
            
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Proveedor");
            
            logger.info("Mostrando formulario de edición para proveedor ID={}", id);
            
            return PROVEEDORES_FORM_VIEW;
            
        } catch (WebClientResponseException.NotFound e) {
            // 404 Not Found
            logger.warn("Proveedor no encontrado: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Proveedor no encontrado");
            return REDIRECT_PROVEEDORES;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener proveedor para edición: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al cargar proveedor: " + obtenerMensajeError(e));
            return REDIRECT_PROVEEDORES;
            
        } catch (Exception e) {
            logger.error("Error inesperado al cargar proveedor para edición", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al cargar proveedor");
            return REDIRECT_PROVEEDORES;
        }
    }

    /**
     * Procesar edición de proveedor.
     * POST /proveedores/{id}/edit
     * Solo accesible para administradores.
     * 
     * @param id ID del proveedor
     * @param proveedor datos del proveedor
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/edit")
    public String editarProveedor(
            @PathVariable Long id,
            @ModelAttribute ProviderDTO proveedor,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó editar proveedor");
            return "redirect:/dashboard";
        }

        try {
            // Asegurar que el ID está establecido
            proveedor.setProveedorId(id);
            
            // Actualizar proveedor vía API REST
            ProviderDTO proveedorActualizado = webClient.put()
                .uri("/proveedores/{id}", id)
                .bodyValue(proveedor)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .block();
            
            logger.info("Proveedor actualizado exitosamente: ID={}, RUC={}, Razón Social={}", 
                id, proveedorActualizado.getRuc(), proveedorActualizado.getRazonSocial());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Proveedor '" + proveedorActualizado.getRazonSocial() + "' actualizado exitosamente");
            
            return REDIRECT_PROVEEDORES;
            
        } catch (WebClientResponseException.NotFound e) {
            // 404 Not Found
            logger.warn("Proveedor no encontrado para actualización: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Proveedor no encontrado");
            return REDIRECT_PROVEEDORES;
            
        } catch (WebClientResponseException.Conflict e) {
            // 409 Conflict - ya existe otro con ese RUC
            logger.warn("Conflicto al actualizar proveedor: ID={}", id);
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Proveedor");
            model.addAttribute("errorMessage", "Ya existe otro proveedor con ese RUC");
            return PROVEEDORES_FORM_VIEW;
            
        } catch (WebClientResponseException.BadRequest e) {
            // 400 Bad Request - datos inválidos
            logger.warn("Datos inválidos al actualizar proveedor: ID={}", id);
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Proveedor");
            model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
            return PROVEEDORES_FORM_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al actualizar proveedor: ID={}, Status={}", id, e.getStatusCode());
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Proveedor");
            model.addAttribute("errorMessage", "Error al actualizar proveedor: " + obtenerMensajeError(e));
            return PROVEEDORES_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar proveedor: ID={}", id, e);
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Proveedor");
            model.addAttribute("errorMessage", "Error inesperado al actualizar proveedor");
            return PROVEEDORES_FORM_VIEW;
        }
    }

    /**
     * Eliminar proveedor.
     * POST /proveedores/{id}/delete
     * Solo accesible para administradores.
     * 
     * @param id ID del proveedor
     * @param session sesión HTTP
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/delete")
    public String eliminarProveedor(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó eliminar proveedor");
            return "redirect:/dashboard";
        }

        try {
            // Primero obtener la razón social del proveedor para el mensaje
            ProviderDTO proveedor = webClient.get()
                .uri("/proveedores/{id}", id)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .block();
            
            String razonSocial = proveedor != null ? proveedor.getRazonSocial() : "ID: " + id;
            
            // Eliminar proveedor vía API REST
            webClient.delete()
                .uri("/proveedores/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            
            logger.info("Proveedor eliminado exitosamente: ID={}, Razón Social={}", id, razonSocial);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Proveedor '" + razonSocial + "' eliminado exitosamente");
            
            return REDIRECT_PROVEEDORES;
            
        } catch (WebClientResponseException.NotFound e) {
            // 404 Not Found
            logger.warn("Proveedor no encontrado para eliminación: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Proveedor no encontrado");
            return REDIRECT_PROVEEDORES;
            
        } catch (WebClientResponseException.UnprocessableEntity e) {
            // 422 Unprocessable Entity - tiene dependencias
            logger.warn("No se puede eliminar proveedor con dependencias: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "No se puede eliminar el proveedor porque tiene lotes asociados");
            return REDIRECT_PROVEEDORES;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al eliminar proveedor: ID={}, Status={}", id, e.getStatusCode());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al eliminar proveedor: " + obtenerMensajeError(e));
            return REDIRECT_PROVEEDORES;
            
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar proveedor: ID={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al eliminar proveedor");
            return REDIRECT_PROVEEDORES;
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtiene todos los proveedores desde el API REST.
     * 
     * @return lista de proveedores
     */
    private List<ProviderDTO> obtenerProveedores() {
        try {
            return webClient.get()
                .uri("/proveedores")
                .retrieve()
                .bodyToFlux(ProviderDTO.class)
                .collectList()
                .block();
                
        } catch (Exception e) {
            logger.error("Error al obtener proveedores desde API", e);
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
            case 409 -> "Conflicto: el RUC ya existe";
            case 422 -> "Error de validación o tiene dependencias asociadas";
            case 500 -> "Error interno del servidor";
            default -> "Error inesperado (código " + e.getStatusCode().value() + ")";
        };
    }
}
