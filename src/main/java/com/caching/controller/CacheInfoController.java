package com.caching.controller;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for retrieving information about the caches managed by the application.
 */
@RestController
@RequestMapping("/cache")
public class CacheInfoController {

    private final CacheManager cacheManager;

    /**
     * Constructs a CacheInfoController with the necessary CacheManager.
     * @param cacheManager The cache manager that oversees all cache operations.
     */
    public CacheInfoController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Provides a list of all active caches and their contents or a relevant message if content display is not possible.
     * This method can be used to inspect the current state and contents of the caches maintained by the application.
     * @return A map containing the names of caches and their respective keys or information messages.
     */
    @GetMapping("/list")
    public Map<String, Object> listCaches() {
        Map<String, Object> cacheDetails = new HashMap<>();

        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Object nativeCache = cache.getNativeCache();

                // For ConcurrentMap-based caches (e.g., default Spring cache)
                if (nativeCache instanceof Map) {
                    Map<?, ?> nativeMap = (Map<?, ?>) nativeCache;
                    cacheDetails.put(cacheName, nativeMap.keySet());
                } else {
                    cacheDetails.put(cacheName, "Cannot display keys for this cache type");
                }
            }
        }

        return cacheDetails;
    }
}
