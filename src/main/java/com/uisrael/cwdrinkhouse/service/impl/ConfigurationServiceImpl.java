package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.configuration.AppConfigurationProperties;
import com.uisrael.cwdrinkhouse.dto.ConfigurationDTO;
import com.uisrael.cwdrinkhouse.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of ConfigurationService.
 * Manages application configuration with caching and backend synchronization.
 * 
 * Provides:
 * - Configuration loading from backend API
 * - Local caching with TTL support
 * - Type-safe configuration access
 * - Configuration updates with backend sync
 * - Cache invalidation and reload capabilities
 * 
 * Requirements: 21.1, 21.2, 21.3, 21.4, 21.5
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
    private static final String BACKEND_CONFIG_ENDPOINT = "/api/v1/configuration";

    private final WebClient webClient;
    private final AppConfigurationProperties configProperties;

    /**
     * Cache for configuration values.
     * Uses ConcurrentHashMap for thread-safe access.
     */
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    /**
     * Timestamp of the last configuration load or update.
     */
    private volatile long lastLoadTime = 0;

    /**
     * Flag indicating if configuration has been loaded.
     */
    private volatile boolean configurationLoaded = false;

    /**
     * Lock for thread-safe configuration loading.
     */
    private final Object loadLock = new Object();

    @Autowired
    public ConfigurationServiceImpl(WebClient webClient, AppConfigurationProperties configProperties) {
        this.webClient = webClient;
        this.configProperties = configProperties;
    }

    @Override
    public void loadConfiguration() {
        if (!configurationLoaded) {
            synchronized (loadLock) {
                if (!configurationLoaded) {
                    try {
                        fetchConfigurationFromBackend();
                        this.configurationLoaded = true;
                        this.lastLoadTime = System.currentTimeMillis();
                        logger.info("Configuration loaded successfully from backend");
                    } catch (Exception e) {
                        logger.warn("Failed to load configuration from backend, initializing with defaults: {}", e.getMessage());
                        // Initialize with defaults instead of throwing exception
                        initializeWithDefaults();
                    }
                }
            }
        }
    }

    @Override
    public String getConfigValue(String key) {
        return getConfigValue(key, null);
    }

    @Override
    public String getConfigValue(String key, String defaultValue) {
        ensureConfigurationLoaded();
        return configCache.getOrDefault(key, defaultValue);
    }

    @Override
    public int getConfigIntValue(String key, int defaultValue) {
        try {
            String value = getConfigValue(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Configuration value for key '{}' is not a valid integer: {}", key, e.getMessage());
            return defaultValue;
        }
    }

    @Override
    public long getConfigLongValue(String key, long defaultValue) {
        try {
            String value = getConfigValue(key);
            return value != null ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Configuration value for key '{}' is not a valid long: {}", key, e.getMessage());
            return defaultValue;
        }
    }

    @Override
    public boolean getConfigBooleanValue(String key, boolean defaultValue) {
        String value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("yes");
    }

    @Override
    public Map<String, String> getAllConfiguration() {
        ensureConfigurationLoaded();
        return new HashMap<>(configCache);
    }

    @Override
    public void updateConfigValue(String key, String value) {
        updateConfiguration(Collections.singletonMap(key, value));
    }

    @Override
    public void updateConfiguration(Map<String, String> configMap) {
        try {
            sendConfigurationToBackend(configMap);
            // Update local cache
            configCache.putAll(configMap);
            this.lastLoadTime = System.currentTimeMillis();
            logger.info("Configuration updated successfully. Updated keys: {}", configMap.keySet());
        } catch (Exception e) {
            logger.error("Failed to update configuration on backend", e);
            throw new RuntimeException("Failed to update configuration on backend", e);
        }
    }

    @Override
    public void reloadConfiguration() {
        synchronized (loadLock) {
            clearCache();
            this.configurationLoaded = false;
            loadConfiguration();
        }
    }

    @Override
    public void clearCache() {
        configCache.clear();
        logger.debug("Configuration cache cleared");
    }

    @Override
    public boolean isConfigurationLoaded() {
        return configurationLoaded;
    }

    @Override
    public long getLastLoadTime() {
        return lastLoadTime;
    }

    /**
     * Fetch configuration from backend API.
     * Makes a GET request to /api/configuration endpoint and populates cache.
     * 
     * @throws Exception if the backend call fails
     */
    private void fetchConfigurationFromBackend() {
        try {
            Map<String, String> configuration = webClient.get()
                    .uri(BACKEND_CONFIG_ENDPOINT)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (configuration != null) {
                // Convert all values to String
                configuration.forEach((key, value) ->
                        configCache.put(key, String.valueOf(value))
                );
                logger.debug("Fetched {} configuration entries from backend", configCache.size());
            } else {
                logger.warn("Received null configuration from backend");
            }
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.warn("Configuration endpoint not found on backend (404)");
                // Initialize with empty config
            } else {
                throw new RuntimeException("Backend returned status " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching configuration from backend", e);
        }
    }

    /**
     * Send configuration updates to backend API.
     * Makes a PUT request to /api/configuration endpoint.
     * 
     * @param configMap map of configuration key-value pairs to update
     * @throws Exception if the backend call fails
     */
    private void sendConfigurationToBackend(Map<String, String> configMap) {
        try {
            webClient.put()
                    .uri(BACKEND_CONFIG_ENDPOINT)
                    .bodyValue(configMap)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.debug("Sent {} configuration updates to backend", configMap.size());
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Backend returned status " + e.getStatusCode() + " while updating configuration: " + 
                    e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error sending configuration updates to backend", e);
        }
    }

    /**
     * Ensure configuration is loaded before accessing.
     * Triggers lazy loading if not yet loaded.
     */
    private void ensureConfigurationLoaded() {
        if (!configurationLoaded) {
            loadConfiguration();
        }
    }

    /**
     * Initialize cache with default configuration values.
     * Can be called during application startup or when configuration fails to load from backend.
     */
    public void initializeWithDefaults() {
        configCache.put("cache.ttl.categories", String.valueOf(configProperties.getCacheTtlCategories()));
        configCache.put("cache.ttl.roles", String.valueOf(configProperties.getCacheTtlRoles()));
        configCache.put("cache.ttl.products", String.valueOf(configProperties.getCacheTtlProducts()));
        configCache.put("cache.ttl.providers", String.valueOf(configProperties.getCacheTtlProviders()));
        configCache.put("cache.max.size", String.valueOf(configProperties.getCacheMaxSize()));
        configCache.put("cache.enabled", String.valueOf(configProperties.isCacheEnabled()));
        configCache.put("backend.url", configProperties.getBackendUrl());
        configCache.put("session.timeout.minutes", String.valueOf(configProperties.getSessionTimeoutMinutes()));
        
        // Mark configuration as loaded to prevent further backend calls
        this.configurationLoaded = true;
        this.lastLoadTime = System.currentTimeMillis();
        
        logger.debug("Configuration cache initialized with default values");
    }

    @Override
    public List<ConfigurationDTO> getAllConfigurationDTOs() {
        ensureConfigurationLoaded();
        return configCache.entrySet().stream()
            .map(entry -> createConfigurationDTO(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(ConfigurationDTO::getCategory)
                             .thenComparing(ConfigurationDTO::getKey))
            .collect(Collectors.toList());
    }

    @Override
    public List<ConfigurationDTO> getConfigurationByCategory(String category) {
        return getAllConfigurationDTOs().stream()
            .filter(config -> category.equals(config.getCategory()))
            .collect(Collectors.toList());
    }

    @Override
    public void updateConfiguration(ConfigurationDTO configurationDTO) {
        updateConfigValue(configurationDTO.getKey(), configurationDTO.getValue());
    }

    @Override
    public long getCacheTtl(String entityType) {
        String key = "cache.ttl." + entityType;
        String value = getConfigValue(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid cache TTL value for {}: {}", entityType, value);
            }
        }

        // Return default values from properties
        switch (entityType.toLowerCase()) {
            case "categories":
                return configProperties.getCacheTtlCategories();
            case "roles":
                return configProperties.getCacheTtlRoles();
            case "products":
                return configProperties.getCacheTtlProducts();
            case "providers":
                return configProperties.getCacheTtlProviders();
            default:
                return 3600; // Default 1 hour
        }
    }

    @Override
    public void updateCacheTtl(String entityType, long ttlSeconds) {
        String key = "cache.ttl." + entityType;
        updateConfigValue(key, String.valueOf(ttlSeconds));
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
