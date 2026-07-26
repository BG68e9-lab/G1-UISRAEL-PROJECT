package com.uisrael.cwdrinkhouse.configuration;

import com.uisrael.cwdrinkhouse.service.impl.ConfigurationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Configuration initializer that sets up default configuration values on application startup.
 * Ensures the configuration system is properly initialized with defaults if backend is not available.
 * 
 * Requirements: 21.1, 21.4, 21.5
 */
@Component
public class ConfigurationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationInitializer.class);

    private final ConfigurationServiceImpl configurationService;

    @Autowired
    public ConfigurationInitializer(ConfigurationServiceImpl configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Initialize configuration on application startup.
     * Attempts to load from backend, falls back to defaults if not available.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeConfiguration() {
        logger.info("Initializing application configuration...");
        
        // Always initialize with defaults first to ensure configuration is available
        configurationService.initializeWithDefaults();
        
        // Log current configuration state
        logConfigurationSummary();
    }

    /**
     * Log a summary of current configuration settings.
     */
    private void logConfigurationSummary() {
        try {
            logger.info("Configuration Summary:");
            logger.info("- Cache enabled: {}", configurationService.getConfigBooleanValue("cache.enabled", true));
            logger.info("- Cache TTL Categories: {} seconds", configurationService.getCacheTtl("categories"));
            logger.info("- Cache TTL Roles: {} seconds", configurationService.getCacheTtl("roles"));
            logger.info("- Cache TTL Products: {} seconds", configurationService.getCacheTtl("products"));
            logger.info("- Cache TTL Providers: {} seconds", configurationService.getCacheTtl("providers"));
            logger.info("- Backend URL: {}", configurationService.getConfigValue("backend.url", "not configured"));
            logger.info("- Session timeout: {} minutes", configurationService.getConfigIntValue("session.timeout.minutes", 30));
            logger.info("- Configuration loaded: {}", configurationService.isConfigurationLoaded());
        } catch (Exception e) {
            logger.error("Error logging configuration summary", e);
        }
    }
}