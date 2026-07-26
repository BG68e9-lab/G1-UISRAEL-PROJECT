package com.uisrael.cwdrinkhouse.controller.web;

import com.uisrael.cwdrinkhouse.controller.BaseController;
import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.ProviderDTO;
import com.uisrael.cwdrinkhouse.dto.ProductDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Web MVC para gestión de órdenes de compra.
 * Consume el API REST usando WebClient y devuelve vistas Thymeleaf.
 * 
 * Este controlador maneja las rutas web para:
 * - Listar órdenes con filtros (estado, fechas)
 * - Crear nuevas órdenes (solo ADMIN)
 * - Editar órdenes en estado BORRADOR (solo ADMIN)
 * - Ver detalles de órdenes
 * - Transiciones de estado: BORRADOR→ENVIADA, ENVIADA→RECIBIDA, →ANULADA
 * - Recibir órdenes (genera lotes e incrementa stock)
 * 
 * Requiere autenticación. Algunas operaciones solo para ADMIN.
 * 
 * Valida: Requisitos 5.1-5.14
 */
@Controller
@RequestMapping("/ordenes")
public class OrdenCompraWebController extends BaseController {

    private static final String ORDERS_LIST_VIEW = "orders/list";
    private static final String ORDERS_FORM_VIEW = "orders/form";
    private static final String ORDERS_DETAIL_VIEW = "orders/detail";
    private static final String ORDERS_RECEIVE_VIEW = "orders/receive";
    private static final String REDIRECT_ORDERS = "redirect:/ordenes";

    @Autowired
    private WebClient webClient;

    /**
     * Listar órdenes con filtros opcionales.
     * GET /ordenes
     * 
     * @param estado filtro por estado (opcional)
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @return vista de lista de órdenes
     */
    @GetMapping
    public String listarOrdenes(
            @RequestParam(required = false) String estado,
            HttpSession session,
            Model model) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            // Obtener órdenes desde API REST con filtro de estado
            List<OrderDTO> ordenes = obtenerOrdenes(estado);
            
            // Agregar atributos al modelo
            model.addAttribute("orders", ordenes);
            model.addAttribute("selectedEstado", estado);
            model.addAttribute("estados", List.of("BORRADOR", "ENVIADA", "RECIBIDA", "ANULADA"));
            
            logger.info("Listando {} órdenes con filtro estado: {}", ordenes.size(), estado);
            
            return ORDERS_LIST_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener órdenes desde API: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al cargar las órdenes: " + obtenerMensajeError(e));
            model.addAttribute("orders", new ArrayList<>());
            model.addAttribute("estados", List.of("BORRADOR", "ENVIADA", "RECIBIDA", "ANULADA"));
            return ORDERS_LIST_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al listar órdenes", e);
            model.addAttribute("errorMessage", "Error inesperado al cargar las órdenes");
            model.addAttribute("orders", new ArrayList<>());
            model.addAttribute("estados", List.of("BORRADOR", "ENVIADA", "RECIBIDA", "ANULADA"));
            return ORDERS_LIST_VIEW;
        }
    }

    /**
     * Mostrar detalles de una orden.
     * GET /ordenes/{id}
     * 
     * @param id ID de la orden
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return vista de detalles
     */
    @GetMapping("/{id}")
    public String verDetalles(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            // Obtener orden desde API REST
            OrderDTO orden = webClient.get()
                .uri("/ordenes-compra/{id}", id)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            model.addAttribute("order", orden);
            model.addAttribute("pageTitle", "Detalle de Orden - " + orden.getCodigoReferencia());
            
            logger.info("Mostrando detalles de orden ID={}, Código={}", id, orden.getCodigoReferencia());
            
            return ORDERS_DETAIL_VIEW;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Orden no encontrada: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Orden no encontrada");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener orden para detalles: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al cargar orden: " + obtenerMensajeError(e));
            return REDIRECT_ORDERS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al cargar orden para detalles", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al cargar orden");
            return REDIRECT_ORDERS;
        }
    }

    /**
     * Mostrar formulario de creación de orden.
     * GET /ordenes/new
     * Solo ADMIN.
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
            logger.warn("Usuario sin permisos de ADMIN intentó crear orden");
            return "redirect:/dashboard";
        }

        try {
            // Obtener proveedores y productos para los selects
            List<ProviderDTO> proveedores = obtenerProveedores();
            List<ProductDTO> productos = obtenerProductos();
            
            // Crear orden vacía
            OrderDTO orden = new OrderDTO();
            
            model.addAttribute("order", orden);
            model.addAttribute("providers", proveedores);
            model.addAttribute("products", productos);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Orden de Compra");
            
            logger.info("Mostrando formulario de creación de orden");
            
            return ORDERS_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error al cargar formulario de creación", e);
            return REDIRECT_ORDERS + "?error=load_form_failed";
        }
    }

    /**
     * Procesar creación de orden.
     * POST /ordenes/new
     * Solo ADMIN.
     * 
     * @param orden datos de la orden
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/new")
    public String crearOrden(
            @ModelAttribute OrderDTO orden,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó crear orden");
            return "redirect:/dashboard";
        }

        try {
            // Crear orden vía API REST
            OrderDTO ordenCreada = webClient.post()
                .uri("/ordenes-compra")
                .bodyValue(orden)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            logger.info("Orden creada exitosamente: ID={}, Código={}", 
                ordenCreada.getOrdenCompraId(), ordenCreada.getCodigoReferencia());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Orden '" + ordenCreada.getCodigoReferencia() + "' creada exitosamente");
            
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.BadRequest e) {
            logger.warn("Datos inválidos al crear orden: {}", e.getMessage());
            try {
                model.addAttribute("providers", obtenerProveedores());
                model.addAttribute("products", obtenerProductos());
            } catch (Exception ex) {
                model.addAttribute("providers", new ArrayList<>());
                model.addAttribute("products", new ArrayList<>());
            }
            model.addAttribute("order", orden);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Orden de Compra");
            model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
            return ORDERS_FORM_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al crear orden: {}", e.getMessage());
            try {
                model.addAttribute("providers", obtenerProveedores());
                model.addAttribute("products", obtenerProductos());
            } catch (Exception ex) {
                model.addAttribute("providers", new ArrayList<>());
                model.addAttribute("products", new ArrayList<>());
            }
            model.addAttribute("order", orden);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Orden de Compra");
            model.addAttribute("errorMessage", "Error al crear orden: " + obtenerMensajeError(e));
            return ORDERS_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al crear orden", e);
            try {
                model.addAttribute("providers", obtenerProveedores());
                model.addAttribute("products", obtenerProductos());
            } catch (Exception ex) {
                model.addAttribute("providers", new ArrayList<>());
                model.addAttribute("products", new ArrayList<>());
            }
            model.addAttribute("order", orden);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Orden de Compra");
            model.addAttribute("errorMessage", "Error inesperado al crear orden");
            return ORDERS_FORM_VIEW;
        }
    }

    /**
     * Mostrar formulario de edición de orden.
     * GET /ordenes/{id}/edit
     * Solo ADMIN y solo órdenes en estado BORRADOR.
     * 
     * @param id ID de la orden
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
            logger.warn("Usuario sin permisos de ADMIN intentó editar orden");
            return "redirect:/dashboard";
        }

        try {
            // Obtener orden desde API REST
            OrderDTO orden = webClient.get()
                .uri("/ordenes-compra/{id}", id)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            // Verificar que la orden esté en estado BORRADOR
            if (!"BORRADOR".equals(orden.getEstado())) {
                logger.warn("Intento de editar orden que no está en BORRADOR: ID={}, Estado={}", 
                    id, orden.getEstado());
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Solo se pueden editar órdenes en estado BORRADOR");
                return REDIRECT_ORDERS;
            }
            
            // Obtener proveedores y productos para los selects
            List<ProviderDTO> proveedores = obtenerProveedores();
            List<ProductDTO> productos = obtenerProductos();
            
            model.addAttribute("order", orden);
            model.addAttribute("providers", proveedores);
            model.addAttribute("products", productos);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Orden - " + orden.getCodigoReferencia());
            
            logger.info("Mostrando formulario de edición para orden ID={}", id);
            
            return ORDERS_FORM_VIEW;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Orden no encontrada: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Orden no encontrada");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener orden para edición: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al cargar orden: " + obtenerMensajeError(e));
            return REDIRECT_ORDERS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al cargar orden para edición", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al cargar orden");
            return REDIRECT_ORDERS;
        }
    }

    /**
     * Procesar edición de orden.
     * POST /ordenes/{id}/edit
     * Solo ADMIN y solo órdenes en estado BORRADOR.
     * 
     * @param id ID de la orden
     * @param orden datos de la orden
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/edit")
    public String editarOrden(
            @PathVariable Long id,
            @ModelAttribute OrderDTO orden,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó editar orden");
            return "redirect:/dashboard";
        }

        try {
            // Asegurar que el ID está establecido
            orden.setOrdenCompraId(id);
            
            // Actualizar orden vía API REST
            OrderDTO ordenActualizada = webClient.put()
                .uri("/ordenes-compra/{id}", id)
                .bodyValue(orden)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            logger.info("Orden actualizada exitosamente: ID={}, Código={}", 
                id, ordenActualizada.getCodigoReferencia());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Orden '" + ordenActualizada.getCodigoReferencia() + "' actualizada exitosamente");
            
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Orden no encontrada para actualización: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Orden no encontrada");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.BadRequest e) {
            logger.warn("Datos inválidos al actualizar orden: ID={}", id);
            try {
                model.addAttribute("providers", obtenerProveedores());
                model.addAttribute("products", obtenerProductos());
            } catch (Exception ex) {
                model.addAttribute("providers", new ArrayList<>());
                model.addAttribute("products", new ArrayList<>());
            }
            model.addAttribute("order", orden);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Orden");
            model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
            return ORDERS_FORM_VIEW;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al actualizar orden: ID={}, Status={}", id, e.getStatusCode());
            try {
                model.addAttribute("providers", obtenerProveedores());
                model.addAttribute("products", obtenerProductos());
            } catch (Exception ex) {
                model.addAttribute("providers", new ArrayList<>());
                model.addAttribute("products", new ArrayList<>());
            }
            model.addAttribute("order", orden);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Orden");
            model.addAttribute("errorMessage", "Error al actualizar orden: " + obtenerMensajeError(e));
            return ORDERS_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar orden: ID={}", id, e);
            try {
                model.addAttribute("providers", obtenerProveedores());
                model.addAttribute("products", obtenerProductos());
            } catch (Exception ex) {
                model.addAttribute("providers", new ArrayList<>());
                model.addAttribute("products", new ArrayList<>());
            }
            model.addAttribute("order", orden);
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Editar Orden");
            model.addAttribute("errorMessage", "Error inesperado al actualizar orden");
            return ORDERS_FORM_VIEW;
        }
    }

    /**
     * Enviar orden (transición BORRADOR → ENVIADA).
     * POST /ordenes/{id}/send
     * Solo ADMIN.
     * 
     * @param id ID de la orden
     * @param session sesión HTTP
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/send")
    public String enviarOrden(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó enviar orden");
            return "redirect:/dashboard";
        }

        try {
            // Enviar orden vía API REST (PATCH)
            OrderDTO ordenEnviada = webClient.patch()
                .uri("/ordenes-compra/{id}/enviar", id)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            logger.info("Orden enviada exitosamente: ID={}, Código={}", id, ordenEnviada.getCodigoReferencia());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Orden '" + ordenEnviada.getCodigoReferencia() + "' enviada exitosamente");
            
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Orden no encontrada para enviar: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Orden no encontrada");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.UnprocessableEntity e) {
            logger.warn("No se puede enviar orden en su estado actual: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "No se puede enviar la orden. Verifique que esté en estado BORRADOR y tenga detalles válidos");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al enviar orden: ID={}, Status={}", id, e.getStatusCode());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al enviar orden: " + obtenerMensajeError(e));
            return REDIRECT_ORDERS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al enviar orden: ID={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al enviar orden");
            return REDIRECT_ORDERS;
        }
    }

    /**
     * Mostrar formulario para recibir orden.
     * GET /ordenes/{id}/receive
     * Solo ADMIN/EMPLEADO.
     * 
     * @param id ID de la orden
     * @param session sesión HTTP
     * @param model modelo para la vista
     * @param redirectAttributes atributos de redirección
     * @return vista de recepción
     */
    @GetMapping("/{id}/receive")
    public String mostrarFormularioRecepcion(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            // Obtener orden desde API REST
            OrderDTO orden = webClient.get()
                .uri("/ordenes-compra/{id}", id)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            // Verificar que la orden esté en estado ENVIADA
            if (!"ENVIADA".equals(orden.getEstado())) {
                logger.warn("Intento de recibir orden que no está en ENVIADA: ID={}, Estado={}", 
                    id, orden.getEstado());
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Solo se pueden recibir órdenes en estado ENVIADA");
                return REDIRECT_ORDERS;
            }
            
            model.addAttribute("order", orden);
            model.addAttribute("pageTitle", "Recibir Orden - " + orden.getCodigoReferencia());
            
            logger.info("Mostrando formulario de recepción para orden ID={}", id);
            
            return ORDERS_RECEIVE_VIEW;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Orden no encontrada: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Orden no encontrada");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener orden para recepción: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al cargar orden: " + obtenerMensajeError(e));
            return REDIRECT_ORDERS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al cargar orden para recepción", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al cargar orden");
            return REDIRECT_ORDERS;
        }
    }

    /**
     * Procesar recepción de orden (transición ENVIADA → RECIBIDA).
     * POST /ordenes/{id}/receive
     * Solo ADMIN/EMPLEADO.
     * Genera lotes e incrementa stock.
     * 
     * @param id ID de la orden
     * @param session sesión HTTP
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/receive")
    public String recibirOrden(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            // Recibir orden vía API REST (PATCH)
            OrderDTO ordenRecibida = webClient.patch()
                .uri("/ordenes-compra/{id}/recibir", id)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            logger.info("Orden recibida exitosamente: ID={}, Código={} - Lotes creados y stock actualizado", 
                id, ordenRecibida.getCodigoReferencia());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Orden '" + ordenRecibida.getCodigoReferencia() + "' recibida exitosamente. " +
                "Se han generado los lotes y actualizado el inventario");
            
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Orden no encontrada para recibir: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Orden no encontrada");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.UnprocessableEntity e) {
            logger.warn("No se puede recibir orden en su estado actual: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "No se puede recibir la orden. Verifique que esté en estado ENVIADA");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al recibir orden: ID={}, Status={}", id, e.getStatusCode());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al recibir orden: " + obtenerMensajeError(e));
            return REDIRECT_ORDERS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al recibir orden: ID={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al recibir orden");
            return REDIRECT_ORDERS;
        }
    }

    /**
     * Anular orden.
     * POST /ordenes/{id}/cancel
     * Solo ADMIN.
     * 
     * @param id ID de la orden
     * @param session sesión HTTP
     * @param redirectAttributes atributos de redirección
     * @return redirección
     */
    @PostMapping("/{id}/cancel")
    public String anularOrden(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Verificar autenticación y rol ADMIN
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        if (!hasAdminRole(session)) {
            logger.warn("Usuario sin permisos de ADMIN intentó anular orden");
            return "redirect:/dashboard";
        }

        try {
            // Obtener orden primero para el mensaje
            OrderDTO orden = webClient.get()
                .uri("/ordenes-compra/{id}", id)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            String codigoReferencia = orden != null ? orden.getCodigoReferencia() : "ID: " + id;
            
            // Anular orden vía API REST (PATCH)
            OrderDTO ordenAnulada = webClient.patch()
                .uri("/ordenes-compra/{id}/anular", id)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
            
            logger.info("Orden anulada exitosamente: ID={}, Código={}", id, codigoReferencia);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Orden '" + codigoReferencia + "' anulada exitosamente");
            
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Orden no encontrada para anular: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Orden no encontrada");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException.UnprocessableEntity e) {
            logger.warn("No se puede anular orden en su estado actual: ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "No se puede anular la orden. Solo se pueden anular órdenes en estado BORRADOR o ENVIADA");
            return REDIRECT_ORDERS;
            
        } catch (WebClientResponseException e) {
            logger.error("Error al anular orden: ID={}, Status={}", id, e.getStatusCode());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al anular orden: " + obtenerMensajeError(e));
            return REDIRECT_ORDERS;
            
        } catch (Exception e) {
            logger.error("Error inesperado al anular orden: ID={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al anular orden");
            return REDIRECT_ORDERS;
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtiene órdenes desde el API REST con filtro de estado opcional.
     * 
     * @param estado filtro por estado
     * @return lista de órdenes
     */
    private List<OrderDTO> obtenerOrdenes(String estado) {
        try {
            return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/ordenes-compra");
                    
                    if (estado != null && !estado.isBlank()) {
                        builder.queryParam("estado", estado);
                    }
                    
                    return builder.build();
                })
                .retrieve()
                .bodyToFlux(OrderDTO.class)
                .collectList()
                .block();
                
        } catch (Exception e) {
            logger.error("Error al obtener órdenes desde API", e);
            return new ArrayList<>();
        }
    }

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
     * Obtiene todos los productos desde el API REST.
     * 
     * @return lista de productos
     */
    private List<ProductDTO> obtenerProductos() {
        try {
            return webClient.get()
                .uri("/productos")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .block();
                
        } catch (Exception e) {
            logger.error("Error al obtener productos desde API", e);
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
            case 422 -> "Error de validación o transición de estado inválida";
            case 500 -> "Error interno del servidor";
            default -> "Error inesperado (código " + e.getStatusCode().value() + ")";
        };
    }
}
