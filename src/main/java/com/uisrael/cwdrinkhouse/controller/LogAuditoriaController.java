package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.dto.AuditLogDTO;
import com.uisrael.cwdrinkhouse.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
// import org.springframework.security.access.prepost.PreAuthorize; // Replaced with SimpleAuth
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing audit logs.
 * Provides comprehensive audit log viewing and filtering capabilities for administrators.
 * 
 * Mapped Routes:
 * - GET /audit - List audit logs with filtering by entidad, accion, date range
 * - GET /audit/{id} - View audit log details with formatted JSON changes
 * - GET /audit/export - Export audit logs to CSV
 * - POST /audit/archive - Archive old audit logs
 * 
 * Requirements: 11.1-11.7, 16.1-16.2
 */
@Controller
@RequestMapping("/auditoria")
// @PreAuthorize("hasRole('ADMIN')") // Replaced with SimpleAuth
public class LogAuditoriaController {

    private static final Logger logger = LoggerFactory.getLogger(LogAuditoriaController.class);

    private final AuditService auditService;

    @Autowired
    public LogAuditoriaController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Display audit logs list with filtering capabilities.
     * Supports filtering by entidad, accion, and date range with pagination.
     * 
     * @param entidad optional entity filter
     * @param accion optional action filter
     * @param fechaInicio optional start date filter
     * @param fechaFin optional end date filter
     * @param page page number (default 0)
     * @param size page size (default 25)
     * @param sort sort criteria (default fechaHora,desc)
     * @param model Spring Model for view data
     * @param request HTTP request for additional context
     * @return the audit list template
     */
    @GetMapping
    public String listAuditLogs(@RequestParam(value = "entidad", required = false) String entidad,
                               @RequestParam(value = "accion", required = false) String accion,
                               @RequestParam(value = "fechaInicio", required = false) 
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
                               @RequestParam(value = "fechaFin", required = false) 
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "25") int size,
                               @RequestParam(value = "sort", defaultValue = "fechaHora,desc") String sort,
                               Model model,
                               HttpServletRequest request) {
        try {
            // Parse sort parameter
            String[] sortParts = sort.split(",");
            String sortProperty = sortParts.length > 0 ? sortParts[0] : "fechaHora";
            Sort.Direction sortDirection = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]) 
                ? Sort.Direction.ASC : Sort.Direction.DESC;

            // Create pageable with sorting
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

            // Get filtered audit logs
            Page<AuditLogDTO> auditLogsPage;
            if (entidad != null || accion != null || fechaInicio != null || fechaFin != null) {
                auditLogsPage = auditService.getAuditLogsFiltered(entidad, accion, fechaInicio, fechaFin, pageable);
            } else {
                auditLogsPage = auditService.getAllAuditLogs(pageable);
            }

            // Get filter options for dropdowns
            List<String> distinctEntidades = auditService.getDistinctEntidades();
            List<String> distinctAcciones = auditService.getDistinctAcciones();

            // Add data to model
            model.addAttribute("auditLogs", auditLogsPage.getContent());
            model.addAttribute("page", auditLogsPage);
            model.addAttribute("distinctEntidades", distinctEntidades);
            model.addAttribute("distinctAcciones", distinctAcciones);
            
            // Current filter values
            model.addAttribute("currentEntidad", entidad);
            model.addAttribute("currentAccion", accion);
            model.addAttribute("currentFechaInicio", fechaInicio);
            model.addAttribute("currentFechaFin", fechaFin);
            model.addAttribute("currentSort", sort);
            model.addAttribute("currentSize", size);

            // Build query string for pagination links
            StringBuilder queryParams = new StringBuilder();
            if (entidad != null && !entidad.trim().isEmpty()) {
                queryParams.append("&entidad=").append(entidad);
            }
            if (accion != null && !accion.trim().isEmpty()) {
                queryParams.append("&accion=").append(accion);
            }
            if (fechaInicio != null) {
                queryParams.append("&fechaInicio=").append(fechaInicio);
            }
            if (fechaFin != null) {
                queryParams.append("&fechaFin=").append(fechaFin);
            }
            queryParams.append("&sort=").append(sort);
            queryParams.append("&size=").append(size);
            
            model.addAttribute("queryParams", queryParams.toString());

            logger.debug("Displaying {} audit logs, page {} of {}", 
                auditLogsPage.getNumberOfElements(), 
                auditLogsPage.getNumber() + 1, 
                auditLogsPage.getTotalPages());

            return "audit/list";
            
        } catch (Exception e) {
            logger.error("Error loading audit logs", e);
            model.addAttribute("error", "Error loading audit logs: " + e.getMessage());
            return "audit/list";
        }
    }

    /**
     * Display audit log details with formatted JSON changes.
     * Shows complete audit information including before/after states and changes.
     * 
     * @param id the audit log ID
     * @param model Spring Model for view data
     * @return the audit detail template
     */
    @GetMapping("/{id}")
    public String viewAuditLogDetail(@PathVariable Long id, Model model) {
        try {
            AuditLogDTO auditLog = auditService.getAuditLogDetail(id);
            
            // Format JSON changes for display
            if (auditLog.hasChanges()) {
                String formattedChanges = auditService.formatJsonChanges(auditLog.getCambios());
                model.addAttribute("formattedChanges", formattedChanges);
            }
            
            model.addAttribute("auditLog", auditLog);
            
            logger.debug("Displaying audit log detail for ID: {}", id);
            return "audit/detail";
            
        } catch (Exception e) {
            logger.error("Error loading audit log detail for ID: " + id, e);
            model.addAttribute("error", "Error loading audit log detail: " + e.getMessage());
            return "redirect:/auditoria";
        }
    }

    /**
     * Export audit logs to CSV format.
     * Allows downloading filtered audit logs for external analysis or compliance.
     * 
     * @param entidad optional entity filter
     * @param accion optional action filter
     * @param fechaInicio optional start date filter
     * @param fechaFin optional end date filter
     * @return CSV response entity
     */
    @GetMapping("/export")
    @ResponseBody
    public String exportAuditLogs(@RequestParam(value = "entidad", required = false) String entidad,
                                 @RequestParam(value = "accion", required = false) String accion,
                                 @RequestParam(value = "fechaInicio", required = false) 
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
                                 @RequestParam(value = "fechaFin", required = false) 
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
                                 HttpServletRequest request) {
        try {
            logger.info("Exporting audit logs with filters: entidad={}, accion={}, fechaInicio={}, fechaFin={}", 
                entidad, accion, fechaInicio, fechaFin);
            
            String csvContent = auditService.exportAuditLogsToCSV(entidad, accion, fechaInicio, fechaFin);
            
            // Set CSV headers
            request.setAttribute("Content-Type", "text/csv");
            request.setAttribute("Content-Disposition", "attachment; filename=audit_logs.csv");
            
            return csvContent;
            
        } catch (Exception e) {
            logger.error("Error exporting audit logs", e);
            return "Error exporting audit logs: " + e.getMessage();
        }
    }

    /**
     * Archive old audit logs.
     * Moves audit logs older than specified date to archive storage.
     * 
     * @param fechaLimite audit logs older than this date will be archived
     * @param redirectAttributes for success/error messages
     * @return redirect to audit list
     */
    @PostMapping("/archive")
    public String archiveOldAuditLogs(@RequestParam("fechaLimite") 
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaLimite,
                                     RedirectAttributes redirectAttributes) {
        try {
            int archivedCount = auditService.archiveOldAuditLogs(fechaLimite);
            
            redirectAttributes.addFlashAttribute("success", 
                "Successfully archived " + archivedCount + " audit logs older than " + fechaLimite);
            
            logger.info("Archived {} audit logs older than {}", archivedCount, fechaLimite);
            
        } catch (Exception e) {
            logger.error("Error archiving audit logs", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error archiving audit logs: " + e.getMessage());
        }

        return "redirect:/auditoria";
    }

    /**
     * Get recent audit logs for AJAX requests.
     * Used by dashboard and other components for real-time audit monitoring.
     * 
     * @param limit maximum number of recent logs (default 10)
     * @return JSON response with recent audit logs
     */
    @GetMapping("/api/recent")
    @ResponseBody
    public List<AuditLogDTO> getRecentAuditLogs(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            return auditService.getRecentAuditLogs(limit);
        } catch (Exception e) {
            logger.error("Error getting recent audit logs", e);
            throw new RuntimeException("Error getting recent audit logs: " + e.getMessage());
        }
    }

    /**
     * Clear filters and show all audit logs.
     * 
     * @return redirect to audit list without filters
     */
    @GetMapping("/clear-filters")
    public String clearFilters() {
        return "redirect:/auditoria";
    }
}
