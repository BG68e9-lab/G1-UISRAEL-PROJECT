package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.ConfigurationDTO;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing application configuration.
 * Handles loading and updating configuration from the backend API.
 * Provides methods to retrieve specific configuration values and reload configuration.
 * 
 * Requirements: 21.1, 21.2, 21.3, 21.4, 21.5
 */
public interface ConfigurationService {

    /**
     * Load all configuration from the backend API.
     * Populates the local configuration cache with values from the backend.
     * 
     * @throws RuntimeException if the backend call fails
     */
    void loadConfiguration();

    /**
     * Get a configuration value by key.
     * Returns the value from cache if available, otherwise retrieves from backend.
     * 
     * @param key the configuration key
     * @return the configuration value as a String, or null if not found
     */
    String getConfigValue(String key);

    /**
     * Get a configuration value by key with a default value.
     * Returns the value from cache if available, otherwise returns the default.
     * 
     * @param key the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value, or the defaultValue if not found
     */
    String getConfigValue(String key, String defaultValue);

    /**
     * Get a configuration value as an integer.
     * 
     * @param key the configuration key
     * @param defaultValue the default value if key not found or not a valid integer
     * @return the configuration value as integer, or the defaultValue if not found/invalid
     */
    int getConfigIntValue(String key, int defaultValue);

    /**
     * Get a configuration value as a long.
     * 
     * @param key the configuration key
     * @param defaultValue the default value if key not found or not a valid long
     * @return the configuration value as long, or the defaultValue if not found/invalid
     */
    long getConfigLongValue(String key, long defaultValue);

    /**
     * Get a configuration value as a boolean.
     * 
     * @param key the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value as boolean, or the defaultValue if not found
     */
    boolean getConfigBooleanValue(String key, boolean defaultValue);

    /**
     * Get all configuration values as a map.
     * 
     * @return map of all configuration key-value pairs
     */
    Map<String, String> getAllConfiguration();

    /**
     * Update a configuration value in the backend.
     * Also updates the local cache after successful update.
     * 
     * @param key the configuration key
     * @param value the new value
     * @throws RuntimeException if the backend call fails
     */
    void updateConfigValue(String key, String value);

    /**
     * Update multiple configuration values in the backend.
     * Also updates the local cache after successful updates.
     * 
     * @param configMap map of key-value pairs to update
     * @throws RuntimeException if the backend call fails
     */
    void updateConfiguration(Map<String, String> configMap);

    /**
     * Reload configuration from the backend.
     * Clears the cache and fetches fresh configuration from the backend API.
     * 
     * @throws RuntimeException if the backend call fails
     */
    void reloadConfiguration();

    /**
     * Clear the local configuration cache.
     * Forces next access to fetch from backend or use defaults.
     */
    void clearCache();

    /**
     * Check if configuration is loaded and valid.
     * 
     * @return true if configuration has been loaded, false otherwise
     */
    boolean isConfigurationLoaded();

    /**
     * Get the timestamp of the last configuration load/update.
     * 
     * @return timestamp in milliseconds, or 0 if not yet loaded
     */
    long getLastLoadTime();

    /**
     * Get all configuration as a list of ConfigurationDTOs with metadata.
     * 
     * @return list of ConfigurationDTO objects with category and type information
     */
    List<ConfigurationDTO> getAllConfigurationDTOs();

    /**
     * Get configuration by category.
     * 
     * @param category the configuration category (e.g., "Cache", "Backend")
     * @return list of ConfigurationDTO objects in the specified category
     */
    List<ConfigurationDTO> getConfigurationByCategory(String category);

    /**
     * Update configuration using ConfigurationDTO.
     * 
     * @param configurationDTO the configuration DTO with updated values
     * @throws RuntimeException if the backend call fails
     */
    void updateConfiguration(ConfigurationDTO configurationDTO);

    /**
     * Get cache TTL value for a specific entity type.
     * 
     * @param entityType the entity type (categories, roles, products, providers)
     * @return TTL in seconds, or default value from properties if not found
     */
    long getCacheTtl(String entityType);

    /**
     * Update cache TTL for a specific entity type.
     * 
     * @param entityType the entity type
     * @param ttlSeconds the new TTL in seconds
     */
    void updateCacheTtl(String entityType, long ttlSeconds);
}
