package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.dto.AlertDTO;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.AlertService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
// import org.springframework.security.access.prepost.PreAuthorize; // Replaced with SimpleAuth
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for managing alerts in the web interface.
 * Handles alert viewing, filtering, and read/unread status management.
 * 
 * Features:
 * - Alert listing with pagination and filtering by type (STOCK_BAJO, VENCIMIENTO_PROXIMO)
 * - Alert filtering by read status (read/unread)
 * - Mark alerts as read/unread functionality
 * - Bulk mark as read for multiple alerts
 * - Dashboard view for recent unread alerts
 * - Priority-based sorting (ALTA, MEDIA, BAJA)
 * - Alert detail view with related entity information
 * 
 * Requirements: 10.1-10.12, 18.5-18.6
 */
@Controller
@RequestMapping("/alertas")
// @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')") // Replaced with SimpleAuth
public class AlertaController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AlertaController.class);

    private final AlertService alertService;

    @Autowired
    public AlertaController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * Display the alerts listing page with filtering and pagination.
     * 
     * @param model the model for template rendering
     * @param tipo optional filter by alert type
     * @param leido optional filter by read status
     * @param prioridad optional filter by priority
     * @param page page number (default 0)
     * @param size page size (default 25)
     * @param sort sort parameter
     * @return the alerts listing template
     */
    @GetMapping
    public String listAlerts(Model model,
                            @RequestParam(value = "tipo", required = false) String tipo,
                            @RequestParam(value = "leido", required = false) Boolean leido,
                            @RequestParam(value = "prioridad", required = false) String prioridad,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "25") int size,
                            @RequestParam(value = "sort", defaultValue = "prioridad,asc") String sort) {
        
        logger.debug("Displaying alerts list - tipo: {}, leido: {}, prioridad: {}, page: {}, size: {}", 
                    tipo, leido, prioridad, page, size);
        
        try {
            // Create pageable with default sorting by priority and creation date
            Sort sortObj = parseSort(sort);
            Pageable pageable = PageRequest.of(page, size, sortObj);
            
            Page<AlertDTO> alertsPage;
            
            if (prioridad != null && !prioridad.trim().isEmpty()) {
                alertsPage = alertService.getAlertsByPriority(prioridad, pageable);
                model.addAttribute("pageTitle", "Alertas - Prioridad " + prioridad);
            } else if (tipo != null && !tipo.trim().isEmpty()) {
                if (leido != null) {
                    alertsPage = alertService.getAlertsByTypeAndReadStatus(tipo, leido, pageable);
                } else {
                    alertsPage = alertService.getAlertsByType(tipo, pageable);
                }
                model.addAttribute("pageTitle", "Alertas - Tipo " + tipo);
            } else if (leido != null) {
                alertsPage = alertService.getAlertsByReadStatus(leido, pageable);
                model.addAttribute("pageTitle", leido ? "Alertas Leídas" : "Alertas No Leídas");
            } else {
                alertsPage = alertService.getAllAlerts(pageable);
                model.addAttribute("pageTitle", "Gestión de Alertas");
            }
            
            // Get unread count for badge
            long unreadCount = alertService.countUnreadAlerts();
            
            model.addAttribute("alertsPage", alertsPage);
            model.addAttribute("alerts", alertsPage.getContent());
            model.addAttribute("unreadCount", unreadCount);
            model.addAttribute("currentTipo", tipo);
            model.addAttribute("currentLeido", leido);
            model.addAttribute("currentPrioridad", prioridad);
            model.addAttribute("currentPage", page);
            model.addAttribute("currentSize", size);
            model.addAttribute("currentSort", sort);
            
            // Add filter options for dropdowns
            model.addAttribute("tipoOptions", List.of("STOCK_BAJO", "VENCIMIENTO_PROXIMO", "SISTEMA", "USUARIO", "NEGOCIO"));
            model.addAttribute("prioridadOptions", List.of("ALTA", "MEDIA", "BAJA"));
            
            return "alertas/listaralertas";
            
        } catch (Exception e) {
            logger.error("Error retrieving alerts list", e);
            model.addAttribute("error", "Error al cargar la lista de alertas: " + e.getMessage());
            model.addAttribute("alertsPage", Page.empty());
            model.addAttribute("alerts", List.of());
            model.addAttribute("unreadCount", 0L);
            return "alertas/listaralertas";
        }
    }

    /**
     * Show alert details.
     * 
     * @param id the alert ID
     * @param model the model for template rendering
     * @return the alert details template
     */
    @GetMapping("/{id}")
    public String showAlertDetails(@PathVariable Long id, Model model) {
        logger.debug("Showing alert details for ID: {}", id);
        
        try {
            AlertDTO alert = alertService.getAlertById(id);
            
            model.addAttribute("alert", alert);
            model.addAttribute("pageTitle", "Detalle de Alerta: " + alert.getTitulo());
            
            return "alertas/detalle";
            
        } catch (EntityNotFoundException e) {
            logger.warn("Alert not found: {}", id);
            model.addAttribute("error", "Alerta no encontrada");
            return "redirect:/alertas";
        } catch (Exception e) {
            logger.error("Error loading alert details: {}", id, e);
            model.addAttribute("error", "Error al cargar los detalles de la alerta: " + e.getMessage());
            return "redirect:/alertas";
        }
    }

    /**
     * Mark an alert as read.
     * 
     * @param id the alert ID
     * @param usuarioEmail the user email (from session)
     * @param redirectAttributes for success/error messages
     * @return redirect to alerts list or alert details
     */
    @PostMapping("/{id}/marcar-leido")
    public String markAsRead(@PathVariable Long id,
                            @RequestParam(value = "usuarioEmail", required = false) String usuarioEmail,
                            @RequestParam(value = "redirect", defaultValue = "list") String redirectTo,
                            RedirectAttributes redirectAttributes) {
        
        logger.debug("Marking alert {} as read by user: {}", id, usuarioEmail);
        
        try {
            // In a real application, usuarioEmail would come from the security context
            if (usuarioEmail == null || usuarioEmail.trim().isEmpty()) {
                usuarioEmail = "sistema@drinkhouse.com"; // Default system user
            }
            
            AlertDTO updatedAlert = alertService.markAlertAsRead(id, usuarioEmail);
            
            logger.info("Alert {} marked as read by user: {}", id, usuarioEmail);
            
            redirectAttributes.addFlashAttribute("success", 
                "Alerta marcada como leída: " + updatedAlert.getTitulo());
            
            if ("details".equals(redirectTo)) {
                return "redirect:/alertas/" + id;
            } else {
                return "redirect:/alertas";
            }
            
        } catch (EntityNotFoundException e) {
            logger.warn("Alert not found for marking as read: {}", id);
            redirectAttributes.addFlashAttribute("error", "Alerta no encontrada");
            return "redirect:/alertas";
            
        } catch (Exception e) {
            logger.error("Error marking alert as read", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al marcar la alerta como leída: " + e.getMessage());
            return "redirect:/alertas";
        }
    }

    /**
     * Mark an alert as unread.
     * 
     * @param id the alert ID
     * @param redirectAttributes for success/error messages
     * @return redirect to alerts list or alert details
     */
    @PostMapping("/{id}/marcar-no-leido")
    public String markAsUnread(@PathVariable Long id,
                              @RequestParam(value = "redirect", defaultValue = "list") String redirectTo,
                              RedirectAttributes redirectAttributes) {
        
        logger.debug("Marking alert {} as unread", id);
        
        try {
            AlertDTO updatedAlert = alertService.markAlertAsUnread(id);
            
            logger.info("Alert {} marked as unread", id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Alerta marcada como no leída: " + updatedAlert.getTitulo());
            
            if ("details".equals(redirectTo)) {
                return "redirect:/alertas/" + id;
            } else {
                return "redirect:/alertas";
            }
            
        } catch (EntityNotFoundException e) {
            logger.warn("Alert not found for marking as unread: {}", id);
            redirectAttributes.addFlashAttribute("error", "Alerta no encontrada");
            return "redirect:/alertas";
            
        } catch (Exception e) {
            logger.error("Error marking alert as unread", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al marcar la alerta como no leída: " + e.getMessage());
            return "redirect:/alertas";
        }
    }

    /**
     * Mark multiple alerts as read (bulk operation).
     * 
     * @param alertIds comma-separated list of alert IDs
     * @param usuarioEmail the user email (from session)
     * @param redirectAttributes for success/error messages
     * @return redirect to alerts list
     */
    @PostMapping("/marcar-leidas-bulk")
    public String markMultipleAsRead(@RequestParam("alertIds") String alertIds,
                                    @RequestParam(value = "usuarioEmail", required = false) String usuarioEmail,
                                    RedirectAttributes redirectAttributes) {
        
        logger.debug("Bulk marking alerts as read: {} by user: {}", alertIds, usuarioEmail);
        
        try {
            if (alertIds == null || alertIds.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No se seleccionaron alertas");
                return "redirect:/alertas";
            }

            // Parse alert IDs
            List<Long> ids = List.of(alertIds.split(","))
                    .stream()
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();

            // In a real application, usuarioEmail would come from the security context
            if (usuarioEmail == null || usuarioEmail.trim().isEmpty()) {
                usuarioEmail = "sistema@drinkhouse.com"; // Default system user
            }
            
            List<AlertDTO> updatedAlerts = alertService.markAlertsAsRead(ids, usuarioEmail);
            
            logger.info("Bulk marked {} alerts as read by user: {}", updatedAlerts.size(), usuarioEmail);
            
            redirectAttributes.addFlashAttribute("success", 
                "Se marcaron " + updatedAlerts.size() + " alertas como leídas");
            
        } catch (NumberFormatException e) {
            logger.warn("Invalid alert IDs format: {}", alertIds);
            redirectAttributes.addFlashAttribute("error", "Formato inválido de IDs de alertas");
            
        } catch (Exception e) {
            logger.error("Error bulk marking alerts as read", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al marcar las alertas como leídas: " + e.getMessage());
        }
        
        return "redirect:/alertas";
    }

    /**
     * Delete an alert.
     * 
     * @param id the alert ID to delete
     * @param redirectAttributes for success/error messages
     * @return redirect to alerts list
     */
    @PostMapping("/{id}/eliminar")
    public String deleteAlert(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Deleting alert with ID: {}", id);
        
        try {
            // Get alert info before deletion for logging
            AlertDTO alert = alertService.getAlertById(id);
            
            alertService.deleteAlert(id);
            
            logger.info("Alert deleted successfully: {} with ID: {}", alert.getTitulo(), id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Alerta eliminada exitosamente: " + alert.getTitulo());
            
        } catch (EntityNotFoundException e) {
            logger.warn("Alert not found for deletion: {}", id);
            redirectAttributes.addFlashAttribute("error", "Alerta no encontrada");
            
        } catch (Exception e) {
            logger.error("Unexpected error deleting alert", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error inesperado al eliminar la alerta: " + e.getMessage());
        }
        
        return "redirect:/alertas";
    }

    /**
     * Show the alert creation form.
     * 
     * @param model the model for template rendering
     * @return the alert creation form template
     */
    @GetMapping("/nueva")
    public String showCreateForm(Model model) {
        logger.debug("Showing alert creation form");
        
        model.addAttribute("alert", new AlertDTO());
        model.addAttribute("pageTitle", "Crear Nueva Alerta");
        model.addAttribute("isEdit", false);
        
        // Add options for dropdowns
        model.addAttribute("tipoOptions", List.of("STOCK_BAJO", "VENCIMIENTO_PROXIMO", "SISTEMA", "USUARIO", "NEGOCIO"));
        model.addAttribute("prioridadOptions", List.of("ALTA", "MEDIA", "BAJA"));
        
        return "alertas/formulario";
    }

    /**
     * Process alert creation form submission.
     * 
     * @param alertDTO the alert data from form
     * @param bindingResult validation results
     * @param redirectAttributes for success/error messages
     * @param model the model for template rendering
     * @return redirect to alerts list or back to form with errors
     */
    @PostMapping
    public String createAlert(@Valid @ModelAttribute("alert") AlertDTO alertDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        logger.debug("Creating new alert: {}", alertDTO.getTitulo());
        
        if (bindingResult.hasErrors()) {
            logger.debug("Validation errors in alert creation form");
            model.addAttribute("pageTitle", "Crear Nueva Alerta");
            model.addAttribute("isEdit", false);
            model.addAttribute("tipoOptions", List.of("STOCK_BAJO", "VENCIMIENTO_PROXIMO", "SISTEMA", "USUARIO", "NEGOCIO"));
            model.addAttribute("prioridadOptions", List.of("ALTA", "MEDIA", "BAJA"));
            return "alertas/formulario";
        }

        try {
            AlertDTO createdAlert = alertService.createAlert(alertDTO);
            
            logger.info("Alert created successfully: {} with ID: {}", 
                       createdAlert.getTitulo(), createdAlert.getAlertaId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Alerta creada exitosamente: " + createdAlert.getTitulo());
            
            return "redirect:/alertas";
            
        } catch (ValidationException e) {
            logger.warn("Validation error creating alert: {}", e.getMessage());
            model.addAttribute("error", "Datos inválidos: " + e.getMessage());
            model.addAttribute("pageTitle", "Crear Nueva Alerta");
            model.addAttribute("isEdit", false);
            model.addAttribute("tipoOptions", List.of("STOCK_BAJO", "VENCIMIENTO_PROXIMO", "SISTEMA", "USUARIO", "NEGOCIO"));
            model.addAttribute("prioridadOptions", List.of("ALTA", "MEDIA", "BAJA"));
            return "alertas/formulario";
            
        } catch (Exception e) {
            logger.error("Unexpected error creating alert", e);
            model.addAttribute("error", "Error inesperado al crear la alerta: " + e.getMessage());
            model.addAttribute("pageTitle", "Crear Nueva Alerta");
            model.addAttribute("isEdit", false);
            model.addAttribute("tipoOptions", List.of("STOCK_BAJO", "VENCIMIENTO_PROXIMO", "SISTEMA", "USUARIO", "NEGOCIO"));
            model.addAttribute("prioridadOptions", List.of("ALTA", "MEDIA", "BAJA"));
            return "alertas/formulario";
        }
    }

    /**
     * Get unread alerts count (AJAX endpoint for badge updates).
     * 
     * @return unread alerts count as JSON
     */
    @GetMapping("/count/unread")
    @ResponseBody
    public Long getUnreadCount() {
        try {
            return alertService.countUnreadAlerts();
        } catch (Exception e) {
            logger.error("Error getting unread alerts count", e);
            return 0L;
        }
    }

    /**
     * Get unread alerts for dashboard (AJAX endpoint).
     * 
     * @param limit maximum number of alerts to return
     * @return list of unread alerts as JSON
     */
    @GetMapping("/dashboard")
    @ResponseBody
    public List<AlertDTO> getDashboardAlerts(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            return alertService.getUnreadAlertsForDashboard(limit);
        } catch (Exception e) {
            logger.error("Error getting dashboard alerts", e);
            return List.of();
        }
    }

    /**
     * Clean expired alerts (admin endpoint).
     * 
     * @param redirectAttributes for success/error messages
     * @return redirect to alerts list
     */
    @PostMapping("/cleanup/expired")
    public String cleanExpiredAlerts(RedirectAttributes redirectAttributes) {
        logger.debug("Cleaning expired alerts");
        
        try {
            int cleanedCount = alertService.cleanExpiredAlerts();
            
            redirectAttributes.addFlashAttribute("success", 
                "Se eliminaron " + cleanedCount + " alertas expiradas");
            
        } catch (Exception e) {
            logger.error("Error cleaning expired alerts", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al limpiar alertas expiradas: " + e.getMessage());
        }
        
        return "redirect:/alertas";
    }

    /**
     * Parses sort parameter string into Sort object.
     * 
     * @param sortParam the sort parameter (e.g., "prioridad,asc")
     * @return Sort object
     */
    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "prioridad").and(Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        }
        
        try {
            String[] parts = sortParam.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()) 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            return Sort.by(direction, property);
        } catch (Exception e) {
            logger.warn("Invalid sort parameter: {}, using default", sortParam);
            return Sort.by(Sort.Direction.ASC, "prioridad").and(Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        }
    }
}
