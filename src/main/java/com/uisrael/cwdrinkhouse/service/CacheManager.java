package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.configuration.AppConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages application-level caching with TTL (Time-To-Live) support.
 * Provides caching for categories and roles with configurable TTL values.
 * Thread-safe with automatic expiration tracking.
 * 
 * Features:
 * - TTL-based cache invalidation
 * - Configurable cache size limits
 * - Automatic expiration checking
 * - Thread-safe concurrent access
 * - Cache statistics and monitoring
 * - Dynamic TTL configuration via ConfigurationService
 * 
 * Requirements: 21.1, 21.3, 21.4, 21.5
 */
@Component
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private final AppConfigurationProperties configProperties;
    private final ConfigurationService configurationService;

    /**
     * Cache entry wrapper that tracks creation time and TTL.
     */
    private static class CacheEntry<T> {
        private final T value;
        private final long createdAt;
        private final long ttlMillis;

        CacheEntry(T value, long ttlSeconds) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
            this.ttlMillis = ttlSeconds * 1000;
        }

        boolean isExpired() {
            long expiryTime = createdAt + ttlMillis;
            return System.currentTimeMillis() > expiryTime;
        }

        long getRemainingTtlMillis() {
            long expiryTime = createdAt + ttlMillis;
            long remaining = expiryTime - System.currentTimeMillis();
            return Math.max(0, remaining);
        }
    }

    /**
     * Main cache storage with concurrent access support.
     */
    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    /**
     * Cache for categories with TTL support.
     */
    private final Map<String, CacheEntry<?>> categoriesCache = new ConcurrentHashMap<>();

    /**
     * Cache for roles with TTL support.
     */
    private final Map<String, CacheEntry<?>> rolesCache = new ConcurrentHashMap<>();

    /**
     * Cache hit/miss statistics.
     */
    private volatile long cacheHits = 0;
    private volatile long cacheMisses = 0;

    @Autowired
    public CacheManager(AppConfigurationProperties configProperties, 
                       ConfigurationService configurationService) {
        this.configProperties = configProperties;
        this.configurationService = configurationService;
    }

    /**
     * Store a value in the cache with default TTL.
     * 
     * @param key the cache key
     * @param value the value to cache
     * @param ttlSeconds the time-to-live in seconds
     */
    public <T> void put(String key, T value, long ttlSeconds) {
        if (!configProperties.isCacheEnabled()) {
            return;
        }

        synchronized (cache) {
            if (cache.size() >= configProperties.getCacheMaxSize()) {
                evictOldestEntry();
            }
        }

        cache.put(key, new CacheEntry<>(value, ttlSeconds));
        logger.debug("Cached key '{}' with TTL {} seconds", key, ttlSeconds);
    }

    /**
     * Retrieve a value from the cache.
     * Returns null if the key doesn't exist or the entry has expired.
     * 
     * @param key the cache key
     * @return the cached value, or null if expired or not found
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (!configProperties.isCacheEnabled()) {
            cacheMisses++;
            return null;
        }

        CacheEntry<?> entry = cache.get(key);
        if (entry == null) {
            cacheMisses++;
            return null;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            cacheMisses++;
            logger.debug("Cache entry '{}' expired and removed", key);
            return null;
        }

        cacheHits++;
        return (T) entry.value;
    }

    /**
     * Check if a cache key exists and hasn't expired.
     * 
     * @param key the cache key
     * @return true if the key exists and is valid, false otherwise
     */
    public boolean containsKey(String key) {
        if (!configProperties.isCacheEnabled()) {
            return false;
        }

        CacheEntry<?> entry = cache.get(key);
        if (entry == null) {
            return false;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            return false;
        }

        return true;
    }

    /**
     * Remove a specific key from the cache.
     * 
     * @param key the cache key to remove
     */
    public void remove(String key) {
        cache.remove(key);
        logger.debug("Removed cache key '{}'", key);
    }

    /**
     * Clear all cache entries.
     */
    public void clear() {
        int size = cache.size();
        cache.clear();
        categoriesCache.clear();
        rolesCache.clear();
        logger.debug("Cleared {} cache entries", size);
    }

    /**
     * Store a category in the categories cache with configured TTL.
     * Uses dynamic TTL from ConfigurationService if available, falls back to properties.
     * 
     * @param key the category cache key
     * @param value the category value
     */
    public <T> void putCategory(String key, T value) {
        if (!configProperties.isCacheEnabled()) {
            return;
        }

        // Use dynamic TTL from configuration service
        long ttl = configurationService.getCacheTtl("categories");
        categoriesCache.put(key, new CacheEntry<>(value, ttl));
        logger.debug("Cached category '{}' with TTL {} seconds (from configuration service)", key, ttl);
    }

    /**
     * Retrieve a category from the categories cache.
     * 
     * @param key the category cache key
     * @return the cached category, or null if expired or not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getCategory(String key) {
        if (!configProperties.isCacheEnabled()) {
            return null;
        }

        CacheEntry<?> entry = categoriesCache.get(key);
        if (entry == null || entry.isExpired()) {
            categoriesCache.remove(key);
            return null;
        }

        return (T) entry.value;
    }

    /**
     * Invalidate all categories in the cache.
     * Call this when categories are updated in the backend.
     */
    public void invalidateCategories() {
        categoriesCache.clear();
        logger.info("Invalidated all category cache entries");
    }

    /**
     * Store a role in the roles cache with configured TTL.
     * Uses dynamic TTL from ConfigurationService if available, falls back to properties.
     * 
     * @param key the role cache key
     * @param value the role value
     */
    public <T> void putRole(String key, T value) {
        if (!configProperties.isCacheEnabled()) {
            return;
        }

        // Use dynamic TTL from configuration service
        long ttl = configurationService.getCacheTtl("roles");
        rolesCache.put(key, new CacheEntry<>(value, ttl));
        logger.debug("Cached role '{}' with TTL {} seconds (from configuration service)", key, ttl);
    }

    /**
     * Retrieve a role from the roles cache.
     * 
     * @param key the role cache key
     * @return the cached role, or null if expired or not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getRole(String key) {
        if (!configProperties.isCacheEnabled()) {
            return null;
        }

        CacheEntry<?> entry = rolesCache.get(key);
        if (entry == null || entry.isExpired()) {
            rolesCache.remove(key);
            return null;
        }

        return (T) entry.value;
    }

    /**
     * Invalidate all roles in the cache.
     * Call this when roles are updated in the backend.
     */
    public void invalidateRoles() {
        rolesCache.clear();
        logger.info("Invalidated all role cache entries");
    }

    /**
     * Store a product in cache with configured TTL.
     * Uses dynamic TTL from ConfigurationService if available.
     * 
     * @param key the product cache key
     * @param value the product value
     */
    public <T> void putProduct(String key, T value) {
        if (!configProperties.isCacheEnabled()) {
            return;
        }

        // Use dynamic TTL from configuration service
        long ttl = configurationService.getCacheTtl("products");
        put(key, value, ttl);
        logger.debug("Cached product '{}' with TTL {} seconds (from configuration service)", key, ttl);
    }

    /**
     * Store a provider in cache with configured TTL.
     * Uses dynamic TTL from ConfigurationService if available.
     * 
     * @param key the provider cache key
     * @param value the provider value
     */
    public <T> void putProvider(String key, T value) {
        if (!configProperties.isCacheEnabled()) {
            return;
        }

        // Use dynamic TTL from configuration service  
        long ttl = configurationService.getCacheTtl("providers");
        put(key, value, ttl);
        logger.debug("Cached provider '{}' with TTL {} seconds (from configuration service)", key, ttl);
    }

    /**
     * Invalidate cache entries by pattern.
     * Useful for invalidating related cache entries when data is updated.
     * 
     * @param keyPattern the key pattern to match (supports wildcards *)
     */
    public void invalidateByPattern(String keyPattern) {
        String regex = keyPattern.replace("*", ".*");
        List<String> keysToRemove = cache.keySet().stream()
                .filter(key -> key.matches(regex))
                .toList();
                
        keysToRemove.forEach(cache::remove);
        logger.info("Invalidated {} cache entries matching pattern '{}'", keysToRemove.size(), keyPattern);
    }

    /**
     * Update cache TTL configuration for a specific entity type.
     * This method can be called when configuration is updated via admin interface.
     * 
     * @param entityType the entity type (categories, roles, products, providers)
     * @param newTtlSeconds the new TTL in seconds
     */
    public void updateCacheTtl(String entityType, long newTtlSeconds) {
        // Update configuration service
        configurationService.updateCacheTtl(entityType, newTtlSeconds);
        
        // Invalidate related cache entries so they use new TTL on next access
        switch (entityType.toLowerCase()) {
            case "categories":
                invalidateCategories();
                break;
            case "roles":
                invalidateRoles();
                break;
            case "products":
                invalidateByPattern("product:*");
                break;
            case "providers":
                invalidateByPattern("provider:*");
                break;
        }
        
        logger.info("Updated cache TTL for {} to {} seconds and invalidated related entries", 
                   entityType, newTtlSeconds);
    }

    /**
     * Get the size of the main cache.
     * 
     * @return number of entries in the cache (including expired entries)
     */
    public int size() {
        return cache.size();
    }

    /**
     * Get the number of categories in the cache.
     * 
     * @return number of entries in the categories cache
     */
    public int categoriesCacheSize() {
        return categoriesCache.size();
    }

    /**
     * Get the number of roles in the cache.
     * 
     * @return number of entries in the roles cache
     */
    public int rolesCacheSize() {
        return rolesCache.size();
    }

    /**
     * Get cache statistics including hit/miss ratio.
     * 
     * @return map containing cache statistics
     */
    public Map<String, Object> getStatistics() {
        long total = cacheHits + cacheMisses;
        double hitRatio = total > 0 ? (double) cacheHits / total : 0;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("cacheSize", cache.size());
        stats.put("categoriesCacheSize", categoriesCache.size());
        stats.put("rolesCacheSize", rolesCache.size());
        stats.put("cacheHits", cacheHits);
        stats.put("cacheMisses", cacheMisses);
        stats.put("totalRequests", total);
        stats.put("hitRatio", String.format("%.2f%%", hitRatio * 100));
        stats.put("cacheEnabled", configProperties.isCacheEnabled());
        
        // Add current TTL configuration values
        stats.put("ttlCategories", configurationService.getCacheTtl("categories"));
        stats.put("ttlRoles", configurationService.getCacheTtl("roles"));
        stats.put("ttlProducts", configurationService.getCacheTtl("products"));
        stats.put("ttlProviders", configurationService.getCacheTtl("providers"));

        return stats;
    }

    /**
     * Get detailed information about a specific cache entry.
     * 
     * @param key the cache key
     * @return map with entry details (value, createdAt, remainingTtl), or null if not found
     */
    public Map<String, Object> getEntryInfo(String key) {
        CacheEntry<?> entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("key", key);
        info.put("value", entry.value);
        info.put("createdAt", new Date(entry.createdAt).toString());
        info.put("expired", entry.isExpired());
        info.put("remainingTtlSeconds", entry.getRemainingTtlMillis() / 1000);

        return info;
    }

    /**
     * Clean up expired entries from all caches.
     * Should be called periodically for maintenance.
     * 
     * @return number of entries removed
     */
    public int cleanupExpiredEntries() {
        int removed = 0;

        // Clean main cache
        removed += cache.values().stream()
                .filter(CacheEntry::isExpired)
                .count();
        cache.values().removeIf(CacheEntry::isExpired);

        // Clean categories cache
        removed += categoriesCache.values().stream()
                .filter(CacheEntry::isExpired)
                .count();
        categoriesCache.values().removeIf(CacheEntry::isExpired);

        // Clean roles cache
        removed += rolesCache.values().stream()
                .filter(CacheEntry::isExpired)
                .count();
        rolesCache.values().removeIf(CacheEntry::isExpired);

        logger.debug("Cleaned up {} expired cache entries", removed);
        return removed;
    }

    /**
     * Evict the oldest entry from the cache to make room for new entries.
     */
    private void evictOldestEntry() {
        String oldestKey = cache.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().createdAt))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (oldestKey != null) {
            cache.remove(oldestKey);
            logger.debug("Evicted oldest cache entry '{}' to make room for new entries", oldestKey);
        }
    }
}
