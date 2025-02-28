package com.caching.controller;

import com.caching.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for cache management operations.
 * Provides endpoints for clearing caches in the system.
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;

    /**
     * Constructs a CacheController with the necessary service for cache operations.
     * @param cacheService The service that provides cache management functionality.
     */
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Clears the specified cache.
     * @param cacheName The name of the cache to clear.
     * @return A ResponseEntity containing a success message.
     */
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        cacheService.clearCache(cacheName);
        return ResponseEntity.ok("Cache cleared successfully.");
    }
}
