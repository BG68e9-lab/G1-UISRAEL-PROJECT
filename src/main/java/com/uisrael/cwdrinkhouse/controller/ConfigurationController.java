package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.dto.ConfigurationDTO;
import com.uisrael.cwdrinkhouse.service.ConfigurationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.access.prepost.PreAuthorize; // Replaced with SimpleAuth
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing application configuration settings.
 * Provides admin interface for viewing and updating configuration parameters.
 * 
 * Mapped Routes:
 * - GET /configuration - List all configuration settings
 * - GET /configuration/edit/{key} - Show edit form for specific configuration
 * - POST /configuration/update - Update configuration value
 * - POST /configuration/reload - Reload configuration from backend
 * 
 * Requirements: 21.1, 21.2, 21.3, 21.4, 21.5
 */
@Controller
@RequestMapping("/configuracion")
// @PreAuthorize("hasRole('ADMIN')") // Replaced with SimpleAuth
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Display configuration settings list.
     * Shows all configuration parameters grouped by category.
     * 
     * @param model Spring Model for view data
     * @return the configuration list template
     */
    @GetMapping
    public String listConfiguration(Model model) {
        try {
            Map<String, String> allConfig = configurationService.getAllConfiguration();
            List<ConfigurationDTO> configList = convertToConfigurationDTOs(allConfig);
            
            model.addAttribute("configurations", configList);
            model.addAttribute("lastLoadTime", configurationService.getLastLoadTime());
            model.addAttribute("configurationLoaded", configurationService.isConfigurationLoaded());
            
            logger.debug("Displaying {} configuration entries", configList.size());
            return "configuration/list";
        } catch (Exception e) {
            logger.error("Error loading configuration list", e);
            model.addAttribute("error", "Error loading configuration: " + e.getMessage());
            return "configuration/list";
        }
    }

    /**
     * Show edit form for a specific configuration entry.
     * 
     * @param key the configuration key to edit
     * @param model Spring Model for view data
     * @return the configuration edit template
     */
    @GetMapping("/edit/{key}")
    public String showEditForm(@PathVariable String key, Model model) {
        try {
            String value = configurationService.getConfigValue(key);
            if (value == null) {
                model.addAttribute("error", "Configuration key '" + key + "' not found");
                return "redirect:/configuracion";
            }

            ConfigurationDTO config = createConfigurationDTO(key, value);
            model.addAttribute("configuration", config);
            
            return "configuration/edit";
        } catch (Exception e) {
            logger.error("Error loading configuration for key: " + key, e);
            model.addAttribute("error", "Error loading configuration: " + e.getMessage());
            return "redirect:/configuracion";
        }
    }

    /**
     * Update configuration value.
     * 
     * @param configuration the configuration DTO with updated values
     * @param bindingResult validation results
     * @param redirectAttributes for success/error messages
     * @return redirect to configuration list
     */
    @PostMapping("/update")
    public String updateConfiguration(@Valid @ModelAttribute ConfigurationDTO configuration,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation errors: " + 
                bindingResult.getAllErrors().toString());
            return "redirect:/configuracion";
        }

        try {
            configurationService.updateConfigValue(configuration.getKey(), configuration.getValue());
            redirectAttributes.addFlashAttribute("success", 
                "Configuration '" + configuration.getKey() + "' updated successfully");
            logger.info("Configuration updated: {} = {}", configuration.getKey(), configuration.getValue());
        } catch (Exception e) {
            logger.error("Error updating configuration: " + configuration.getKey(), e);
            redirectAttributes.addFlashAttribute("error", 
                "Error updating configuration: " + e.getMessage());
        }

        return "redirect:/configuracion";
    }

    /**
     * Reload configuration from backend.
     * 
     * @param redirectAttributes for success/error messages
     * @return redirect to configuration list
     */
    @PostMapping("/reload")
    public String reloadConfiguration(RedirectAttributes redirectAttributes) {
        try {
            configurationService.reloadConfiguration();
            redirectAttributes.addFlashAttribute("success", "Configuration reloaded successfully");
            logger.info("Configuration reloaded from backend");
        } catch (Exception e) {
            logger.error("Error reloading configuration", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error reloading configuration: " + e.getMessage());
        }

        return "redirect:/configuracion";
    }

    /**
     * Clear configuration cache.
     * 
     * @param redirectAttributes for success/error messages
     * @return redirect to configuration list
     */
    @PostMapping("/clear-cache")
    public String clearCache(RedirectAttributes redirectAttributes) {
        try {
            configurationService.clearCache();
            redirectAttributes.addFlashAttribute("success", "Configuration cache cleared");
            logger.info("Configuration cache cleared");
        } catch (Exception e) {
            logger.error("Error clearing configuration cache", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error clearing cache: " + e.getMessage());
        }

        return "redirect:/configuracion";
    }

    /**
     * Get configuration value via AJAX.
     * 
     * @param key the configuration key
     * @return JSON response with configuration value
     */
    @GetMapping("/api/{key}")
    @ResponseBody
    public Map<String, Object> getConfigurationValue(@PathVariable String key) {
        try {
            String value = configurationService.getConfigValue(key);
            return Map.of(
                "success", true,
                "key", key,
                "value", value != null ? value : "",
                "found", value != null
            );
        } catch (Exception e) {
            logger.error("Error getting configuration value for key: " + key, e);
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    /**
     * Convert configuration map to list of ConfigurationDTOs.
     * 
     * @param configMap the configuration map
     * @return list of ConfigurationDTO objects
     */
    private List<ConfigurationDTO> convertToConfigurationDTOs(Map<String, String> configMap) {
        List<ConfigurationDTO> configList = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            ConfigurationDTO config = createConfigurationDTO(entry.getKey(), entry.getValue());
            configList.add(config);
        }
        
        // Sort by category and then by key
        configList.sort((c1, c2) -> {
            int categoryCompare = c1.getCategory().compareTo(c2.getCategory());
            return categoryCompare != 0 ? categoryCompare : c1.getKey().compareTo(c2.getKey());
        });
        
        return configList;
    }

    /**
     * Create ConfigurationDTO with metadata based on key patterns.
     * 
     * @param key the configuration key
     * @param value the configuration value
     * @return ConfigurationDTO with appropriate metadata
     */
    private ConfigurationDTO createConfigurationDTO(String key, String value) {
        ConfigurationDTO config = new ConfigurationDTO(key, value);
        
        // Determine category and metadata based on key patterns
        if (key.startsWith("cache.")) {
            config.setCategory("Cache");
            config.setRequiresRestart(false);
            if (key.contains("ttl")) {
                config.setDescription("Cache TTL in seconds");
                config.setDataType("LONG");
            } else if (key.contains("enabled")) {
                config.setDescription("Enable/disable caching");
                config.setDataType("BOOLEAN");
            } else if (key.contains("size")) {
                config.setDescription("Maximum cache size");
                config.setDataType("INTEGER");
            }
        } else if (key.startsWith("backend.")) {
            config.setCategory("Backend");
            config.setRequiresRestart(true);
            if (key.contains("url")) {
                config.setDescription("Backend API URL");
                config.setDataType("STRING");
            } else if (key.contains("timeout")) {
                config.setDescription("Backend timeout in seconds");
                config.setDataType("INTEGER");
            }
        } else if (key.startsWith("session.")) {
            config.setCategory("Session");
            config.setRequiresRestart(true);
            config.setDescription("Session configuration");
            config.setDataType("INTEGER");
        } else if (key.startsWith("retry.")) {
            config.setCategory("Retry");
            config.setRequiresRestart(false);
            config.setDescription("Retry configuration");
            config.setDataType("INTEGER");
        } else {
            config.setCategory("General");
            config.setRequiresRestart(false);
            config.setDataType("STRING");
        }
        
        config.setIsEditable(true);
        return config;
    }
}