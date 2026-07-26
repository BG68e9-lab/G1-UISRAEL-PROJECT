package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for configuration settings.
 * Represents application configuration values that can be loaded from and updated to the backend.
 * Used in configuration forms and REST API communication.
 * 
 * Requirements: 21.1, 21.2, 21.3, 21.4, 21.5
 */
public class ConfigurationDTO {

    /**
     * Configuration key identifier.
     */
    @NotBlank(message = "Configuration key is required")
    private String key;

    /**
     * Configuration value.
     */
    @NotBlank(message = "Configuration value is required")
    private String value;

    /**
     * Configuration description for admin interface.
     */
    private String description;

    /**
     * Configuration category for grouping.
     */
    private String category;

    /**
     * Data type of the configuration value (STRING, INTEGER, LONG, BOOLEAN).
     */
    private String dataType;

    /**
     * Whether this configuration requires application restart to take effect.
     */
    private Boolean requiresRestart;

    /**
     * Whether this configuration can be updated by admins.
     */
    private Boolean isEditable;

    /**
     * Default constructor.
     */
    public ConfigurationDTO() {
    }

    /**
     * Constructor with key and value.
     * 
     * @param key the configuration key
     * @param value the configuration value
     */
    public ConfigurationDTO(String key, String value) {
        this.key = key;
        this.value = value;
        this.isEditable = true;
        this.requiresRestart = false;
        this.dataType = "STRING";
    }

    /**
     * Full constructor.
     * 
     * @param key the configuration key
     * @param value the configuration value
     * @param description the configuration description
     * @param category the configuration category
     * @param dataType the data type
     * @param requiresRestart whether restart is required
     * @param isEditable whether configuration is editable
     */
    public ConfigurationDTO(String key, String value, String description, String category, 
                          String dataType, Boolean requiresRestart, Boolean isEditable) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.category = category;
        this.dataType = dataType;
        this.requiresRestart = requiresRestart;
        this.isEditable = isEditable;
    }

    // Getters and Setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Boolean getRequiresRestart() {
        return requiresRestart;
    }

    public void setRequiresRestart(Boolean requiresRestart) {
        this.requiresRestart = requiresRestart;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    /**
     * Get value as integer.
     * 
     * @return the value as integer, or throws NumberFormatException if invalid
     */
    public Integer getIntValue() {
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    /**
     * Get value as long.
     * 
     * @return the value as long, or throws NumberFormatException if invalid
     */
    public Long getLongValue() {
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
    }

    /**
     * Get value as boolean.
     * 
     * @return the value as boolean
     */
    public Boolean getBooleanValue() {
        if (value == null) {
            return null;
        }
        return value.equalsIgnoreCase("true") || 
               value.equalsIgnoreCase("1") || 
               value.equalsIgnoreCase("yes");
    }

    /**
     * Static factory method for cache TTL configurations.
     * 
     * @param entityType the entity type (categories, roles, etc.)
     * @param ttlSeconds the TTL in seconds
     * @return ConfigurationDTO for cache TTL
     */
    public static ConfigurationDTO createCacheTtl(String entityType, long ttlSeconds) {
        return new ConfigurationDTO(
            "cache.ttl." + entityType,
            String.valueOf(ttlSeconds),
            "Cache TTL for " + entityType + " in seconds",
            "Cache",
            "LONG",
            false,
            true
        );
    }

    /**
     * Static factory method for backend timeout configurations.
     * 
     * @param timeoutType the timeout type (connect, read, write)
     * @param timeoutSeconds the timeout in seconds
     * @return ConfigurationDTO for backend timeout
     */
    public static ConfigurationDTO createBackendTimeout(String timeoutType, int timeoutSeconds) {
        return new ConfigurationDTO(
            "backend." + timeoutType + ".timeout",
            String.valueOf(timeoutSeconds),
            "Backend " + timeoutType + " timeout in seconds",
            "Backend",
            "INTEGER",
            true,
            true
        );
    }

    @Override
    public String toString() {
        return "ConfigurationDTO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", dataType='" + dataType + '\'' +
                ", requiresRestart=" + requiresRestart +
                ", isEditable=" + isEditable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationDTO that = (ConfigurationDTO) o;
        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}