package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.dto.RoleDTO;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.ConflictException;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.RoleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for managing roles in the web interface.
 * Handles role CRUD operations with proper error handling and validation.
 * 
 * Features:
 * - Role listing with filtering options
 * - Role creation with duplicate name checking (409 Conflict handling)
 * - Role editing with system role protection
 * - Role deletion with business rule validation
 * - System vs custom role differentiation
 * - Proper error message display to users
 * 
 * Requirements: 9.1-9.6, 16.1-16.4
 */
@Controller
@RequestMapping("/roles")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);

    private final RoleService roleService;

    @Autowired
    public RolController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Display the roles listing page.
     * Shows all roles with options to filter by active status and system/custom type.
     * 
     * @param model the model for template rendering
     * @param activeFilter optional filter for active status
     * @param typeFilter optional filter for role type (system/custom)
     * @return the roles listing template
     */
    @GetMapping
    public String listRoles(Model model,
                           @RequestParam(value = "active", required = false) Boolean activeFilter,
                           @RequestParam(value = "type", required = false) String typeFilter) {
        
        logger.debug("Displaying roles list - activeFilter: {}, typeFilter: {}", activeFilter, typeFilter);
        
        try {
            List<RoleDTO> roles;
            
            if ("system".equals(typeFilter)) {
                roles = roleService.getSystemRoles();
                model.addAttribute("pageTitle", "Roles del Sistema");
            } else if ("custom".equals(typeFilter)) {
                roles = roleService.getCustomRoles();
                model.addAttribute("pageTitle", "Roles Personalizados");
            } else if (activeFilter != null) {
                roles = roleService.getRolesByActiveStatus(activeFilter);
                model.addAttribute("pageTitle", activeFilter ? "Roles Activos" : "Roles Inactivos");
            } else {
                roles = roleService.getAllRoles();
                model.addAttribute("pageTitle", "Gestión de Roles");
            }
            
            model.addAttribute("roles", roles);
            model.addAttribute("activeFilter", activeFilter);
            model.addAttribute("typeFilter", typeFilter);
            model.addAttribute("newRole", new RoleDTO()); // For creation form
            
            return "roles/listarroles";
            
        } catch (Exception e) {
            logger.error("Error retrieving roles list", e);
            model.addAttribute("error", "Error al cargar la lista de roles: " + e.getMessage());
            model.addAttribute("roles", List.of());
            model.addAttribute("newRole", new RoleDTO());
            return "roles/listarroles";
        }
    }

    /**
     * Show the role creation form.
     * 
     * @param model the model for template rendering
     * @return the role creation form template
     */
    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        logger.debug("Showing role creation form");
        
        model.addAttribute("role", new RoleDTO());
        model.addAttribute("pageTitle", "Crear Nuevo Rol");
        model.addAttribute("isEdit", false);
        
        return "roles/formulario";
    }

    /**
     * Process role creation form submission.
     * Validates the role data and handles duplicate name conflicts.
     * 
     * @param roleDTO the role data from form
     * @param bindingResult validation results
     * @param redirectAttributes for success/error messages
     * @param model the model for template rendering
     * @return redirect to roles list or back to form with errors
     */
    @PostMapping
    public String createRole(@Valid @ModelAttribute("role") RoleDTO roleDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        logger.debug("Creating new role: {}", roleDTO.getNombre());
        
        if (bindingResult.hasErrors()) {
            logger.debug("Validation errors in role creation form");
            model.addAttribute("pageTitle", "Crear Nuevo Rol");
            model.addAttribute("isEdit", false);
            return "roles/formulario";
        }

        try {
            // Check for duplicate name
            if (roleService.roleNameExists(roleDTO.getNombre(), null)) {
                bindingResult.rejectValue("nombre", "error.duplicate", 
                    "Ya existe un rol con el nombre: " + roleDTO.getNombre());
                model.addAttribute("pageTitle", "Crear Nuevo Rol");
                model.addAttribute("isEdit", false);
                return "roles/formulario";
            }

            RoleDTO createdRole = roleService.createRole(roleDTO);
            
            logger.info("Role created successfully: {} with ID: {}", 
                       createdRole.getNombre(), createdRole.getRolId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Rol creado exitosamente: " + createdRole.getNombre());
            
            return "redirect:/roles";
            
        } catch (ConflictException e) {
            logger.warn("Conflict creating role: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "error.duplicate", 
                "El nombre del rol ya existe");
            model.addAttribute("pageTitle", "Crear Nuevo Rol");
            model.addAttribute("isEdit", false);
            return "roles/formulario";
            
        } catch (ValidationException e) {
            logger.warn("Validation error creating role: {}", e.getMessage());
            model.addAttribute("error", "Datos inválidos: " + e.getMessage());
            model.addAttribute("pageTitle", "Crear Nuevo Rol");
            model.addAttribute("isEdit", false);
            return "roles/formulario";
            
        } catch (Exception e) {
            logger.error("Unexpected error creating role", e);
            model.addAttribute("error", "Error inesperado al crear el rol: " + e.getMessage());
            model.addAttribute("pageTitle", "Crear Nuevo Rol");
            model.addAttribute("isEdit", false);
            return "roles/formulario";
        }
    }

    /**
     * Show the role editing form.
     * 
     * @param id the role ID to edit
     * @param model the model for template rendering
     * @return the role editing form template
     */
    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Showing edit form for role ID: {}", id);
        
        try {
            RoleDTO role = roleService.getRoleById(id);
            
            model.addAttribute("role", role);
            model.addAttribute("pageTitle", "Editar Rol: " + role.getNombre());
            model.addAttribute("isEdit", true);
            
            return "roles/formulario";
            
        } catch (EntityNotFoundException e) {
            logger.warn("Role not found for editing: {}", id);
            model.addAttribute("error", "Rol no encontrado");
            return "redirect:/roles";
        } catch (Exception e) {
            logger.error("Error loading role for editing: {}", id, e);
            model.addAttribute("error", "Error al cargar el rol: " + e.getMessage());
            return "redirect:/roles";
        }
    }

    /**
     * Process role update form submission.
     * Validates the role data and handles business rule violations.
     * 
     * @param id the role ID to update
     * @param roleDTO the updated role data
     * @param bindingResult validation results
     * @param redirectAttributes for success/error messages
     * @param model the model for template rendering
     * @return redirect to roles list or back to form with errors
     */
    @PostMapping("/{id}")
    public String updateRole(@PathVariable Long id,
                            @Valid @ModelAttribute("role") RoleDTO roleDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        logger.debug("Updating role with ID: {}", id);
        
        if (bindingResult.hasErrors()) {
            logger.debug("Validation errors in role update form");
            model.addAttribute("pageTitle", "Editar Rol");
            model.addAttribute("isEdit", true);
            return "roles/formulario";
        }

        try {
            // Check for duplicate name (excluding current role)
            if (roleService.roleNameExists(roleDTO.getNombre(), id)) {
                bindingResult.rejectValue("nombre", "error.duplicate", 
                    "Ya existe un rol con el nombre: " + roleDTO.getNombre());
                model.addAttribute("pageTitle", "Editar Rol");
                model.addAttribute("isEdit", true);
                return "roles/formulario";
            }

            RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
            
            logger.info("Role updated successfully: {} with ID: {}", 
                       updatedRole.getNombre(), updatedRole.getRolId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Rol actualizado exitosamente: " + updatedRole.getNombre());
            
            return "redirect:/roles";
            
        } catch (EntityNotFoundException e) {
            logger.warn("Role not found for update: {}", id);
            redirectAttributes.addFlashAttribute("error", "Rol no encontrado");
            return "redirect:/roles";
            
        } catch (ConflictException e) {
            logger.warn("Conflict updating role: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "error.duplicate", 
                "El nombre del rol ya existe");
            model.addAttribute("pageTitle", "Editar Rol");
            model.addAttribute("isEdit", true);
            return "roles/formulario";
            
        } catch (BusinessRuleException e) {
            logger.warn("Business rule violation updating role: {}", e.getMessage());
            model.addAttribute("error", "No se puede modificar este rol: " + e.getMessage());
            model.addAttribute("pageTitle", "Editar Rol");
            model.addAttribute("isEdit", true);
            return "roles/formulario";
            
        } catch (ValidationException e) {
            logger.warn("Validation error updating role: {}", e.getMessage());
            model.addAttribute("error", "Datos inválidos: " + e.getMessage());
            model.addAttribute("pageTitle", "Editar Rol");
            model.addAttribute("isEdit", true);
            return "roles/formulario";
            
        } catch (Exception e) {
            logger.error("Unexpected error updating role", e);
            model.addAttribute("error", "Error inesperado al actualizar el rol: " + e.getMessage());
            model.addAttribute("pageTitle", "Editar Rol");
            model.addAttribute("isEdit", true);
            return "roles/formulario";
        }
    }

    /**
     * Delete a role.
     * Validates that system roles cannot be deleted and handles business rule violations.
     * 
     * @param id the role ID to delete
     * @param redirectAttributes for success/error messages
     * @return redirect to roles list
     */
    @PostMapping("/{id}/eliminar")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Deleting role with ID: {}", id);
        
        try {
            // Get role info before deletion for logging
            RoleDTO role = roleService.getRoleById(id);
            
            roleService.deleteRole(id);
            
            logger.info("Role deleted successfully: {} with ID: {}", role.getNombre(), id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Rol eliminado exitosamente: " + role.getNombre());
            
        } catch (EntityNotFoundException e) {
            logger.warn("Role not found for deletion: {}", id);
            redirectAttributes.addFlashAttribute("error", "Rol no encontrado");
            
        } catch (BusinessRuleException e) {
            logger.warn("Cannot delete role due to business rules: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                "No se puede eliminar el rol: " + e.getMessage());
            
        } catch (Exception e) {
            logger.error("Unexpected error deleting role", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error inesperado al eliminar el rol: " + e.getMessage());
        }
        
        return "redirect:/roles";
    }

    /**
     * Get role details (AJAX endpoint).
     * 
     * @param id the role ID
     * @return the role DTO as JSON
     */
    @GetMapping("/{id}")
    @ResponseBody
    public RoleDTO getRoleDetails(@PathVariable Long id) {
        logger.debug("Getting role details for ID: {}", id);
        
        try {
            return roleService.getRoleById(id);
        } catch (EntityNotFoundException e) {
            logger.warn("Role not found: {}", id);
            throw e;
        }
    }

    /**
     * Refresh roles cache (admin endpoint).
     * 
     * @param redirectAttributes for success/error messages
     * @return redirect to roles list
     */
    @PostMapping("/cache/refresh")
    public String refreshCache(RedirectAttributes redirectAttributes) {
        logger.debug("Refreshing roles cache");
        
        try {
            List<RoleDTO> roles = roleService.refreshRolesCache();
            
            redirectAttributes.addFlashAttribute("success", 
                "Cache actualizado. Se cargaron " + roles.size() + " roles.");
            
        } catch (Exception e) {
            logger.error("Error refreshing roles cache", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar la cache: " + e.getMessage());
        }
        
        return "redirect:/roles";
    }
}
