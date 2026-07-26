package com.uisrael.cwdrinkhouse.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Centralized application configuration properties.
 * Binds to application.properties/yml with prefix "app.config".
 * 
 * Example configuration:
 * app.config.cache-ttl-categories=3600
 * app.config.cache-ttl-roles=3600
 * app.config.cache-ttl-products=1800
 * app.config.cache-ttl-providers=1800
 * 
 * Requirements: 21.1, 21.2, 21.3, 21.4, 21.5
 */
@Component
@ConfigurationProperties(prefix = "app.config")
public class AppConfigurationProperties {

    /**
     * Cache TTL (Time-To-Live) for category data in seconds.
     * Default: 3600 seconds (1 hour)
     */
    private long cacheTtlCategories = 3600;

    /**
     * Cache TTL (Time-To-Live) for role data in seconds.
     * Default: 3600 seconds (1 hour)
     */
    private long cacheTtlRoles = 3600;

    /**
     * Cache TTL (Time-To-Live) for product data in seconds.
     * Default: 1800 seconds (30 minutes)
     */
    private long cacheTtlProducts = 1800;

    /**
     * Cache TTL (Time-To-Live) for provider data in seconds.
     * Default: 1800 seconds (30 minutes)
     */
    private long cacheTtlProviders = 1800;

    /**
     * Maximum number of items in cache.
     * Default: 1000
     */
    private int cacheMaxSize = 1000;

    /**
     * Enable or disable caching.
     * Default: true
     */
    private boolean cacheEnabled = true;

    /**
     * Backend API base URL for service calls.
     * Default: http://localhost:8080
     */
    private String backendUrl = "http://localhost:8080";

    /**
     * Backend API connection timeout in seconds.
     * Default: 10
     */
    private int backendConnectTimeout = 10;

    /**
     * Backend API read timeout in seconds.
     * Default: 10
     */
    private int backendReadTimeout = 10;

    /**
     * Backend API write timeout in seconds.
     * Default: 10
     */
    private int backendWriteTimeout = 10;

    /**
     * Maximum retry attempts for transient failures.
     * Default: 3
     */
    private int retryMaxAttempts = 3;

    /**
     * Retry backoff duration in milliseconds.
     * Default: 1000
     */
    private long retryBackoffDuration = 1000;

    /**
     * Session timeout in minutes.
     * Default: 30
     */
    private int sessionTimeoutMinutes = 30;

    /**
     * Enable debug logging.
     * Default: false
     */
    private boolean debugLoggingEnabled = false;

    // Getters and Setters

    public long getCacheTtlCategories() {
        return cacheTtlCategories;
    }

    public void setCacheTtlCategories(long cacheTtlCategories) {
        this.cacheTtlCategories = cacheTtlCategories;
    }

    public long getCacheTtlRoles() {
        return cacheTtlRoles;
    }

    public void setCacheTtlRoles(long cacheTtlRoles) {
        this.cacheTtlRoles = cacheTtlRoles;
    }

    public long getCacheTtlProducts() {
        return cacheTtlProducts;
    }

    public void setCacheTtlProducts(long cacheTtlProducts) {
        this.cacheTtlProducts = cacheTtlProducts;
    }

    public long getCacheTtlProviders() {
        return cacheTtlProviders;
    }

    public void setCacheTtlProviders(long cacheTtlProviders) {
        this.cacheTtlProviders = cacheTtlProviders;
    }

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public String getBackendUrl() {
        return backendUrl;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public int getBackendConnectTimeout() {
        return backendConnectTimeout;
    }

    public void setBackendConnectTimeout(int backendConnectTimeout) {
        this.backendConnectTimeout = backendConnectTimeout;
    }

    public int getBackendReadTimeout() {
        return backendReadTimeout;
    }

    public void setBackendReadTimeout(int backendReadTimeout) {
        this.backendReadTimeout = backendReadTimeout;
    }

    public int getBackendWriteTimeout() {
        return backendWriteTimeout;
    }

    public void setBackendWriteTimeout(int backendWriteTimeout) {
        this.backendWriteTimeout = backendWriteTimeout;
    }

    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public void setRetryMaxAttempts(int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }

    public long getRetryBackoffDuration() {
        return retryBackoffDuration;
    }

    public void setRetryBackoffDuration(long retryBackoffDuration) {
        this.retryBackoffDuration = retryBackoffDuration;
    }

    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }

    public void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }

    public boolean isDebugLoggingEnabled() {
        return debugLoggingEnabled;
    }

    public void setDebugLoggingEnabled(boolean debugLoggingEnabled) {
        this.debugLoggingEnabled = debugLoggingEnabled;
    }

    /**
     * Get cache TTL as Duration object.
     * 
     * @return Duration representing cache TTL for categories
     */
    public Duration getCacheTtlCategoriesDuration() {
        return Duration.ofSeconds(cacheTtlCategories);
    }

    /**
     * Get cache TTL as Duration object.
     * 
     * @return Duration representing cache TTL for roles
     */
    public Duration getCacheTtlRolesDuration() {
        return Duration.ofSeconds(cacheTtlRoles);
    }

    /**
     * Get cache TTL as Duration object.
     * 
     * @return Duration representing cache TTL for products
     */
    public Duration getCacheTtlProductsDuration() {
        return Duration.ofSeconds(cacheTtlProducts);
    }

    /**
     * Get cache TTL as Duration object.
     * 
     * @return Duration representing cache TTL for providers
     */
    public Duration getCacheTtlProvidersDuration() {
        return Duration.ofSeconds(cacheTtlProviders);
    }

    @Override
    public String toString() {
        return "AppConfigurationProperties{" +
                "cacheTtlCategories=" + cacheTtlCategories +
                ", cacheTtlRoles=" + cacheTtlRoles +
                ", cacheTtlProducts=" + cacheTtlProducts +
                ", cacheTtlProviders=" + cacheTtlProviders +
                ", cacheMaxSize=" + cacheMaxSize +
                ", cacheEnabled=" + cacheEnabled +
                ", backendUrl='" + backendUrl + '\'' +
                ", backendConnectTimeout=" + backendConnectTimeout +
                ", backendReadTimeout=" + backendReadTimeout +
                ", backendWriteTimeout=" + backendWriteTimeout +
                ", retryMaxAttempts=" + retryMaxAttempts +
                ", retryBackoffDuration=" + retryBackoffDuration +
                ", sessionTimeoutMinutes=" + sessionTimeoutMinutes +
                ", debugLoggingEnabled=" + debugLoggingEnabled +
                '}';
    }
}
