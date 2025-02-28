package com.caching.util;

/**
 * Constants for geocoding-related operations.
 */
public class Constants {
    // Cache name for geocoding results
    public static final String GEOCODING_CACHE = "geocoding";

    // Cache name for reverse geocoding results
    public static final String REVERSE_GEOCODING_CACHE = "reverse-geocoding";

    // Cache key pattern for forward geocoding using an address
    public static final String FORWARD_GEOCODING_KEY = "#address";

    // Cache key pattern for reverse geocoding using latitude and longitude
    public static final String REVERSE_GEOCODING_KEY = "{#latitude,#longitude}";

    // Base URL for the external geocoding API
    public static final String EXTERNAL_API_BASE_URL = "https://api.positionstack.com/v1";

    // Access key for the external geocoding API
    public static final String API_ACCESS_KEY = "4211bf2079d8572d7d10fbe430109e7f";

    // Constant for the 'latitude' parameter
    public static final String LATITUDE = "latitude";

    // Constant for the 'longitude' parameter
    public static final String LONGITUDE = "longitude";

    // Private constructor to prevent instantiation
    private Constants() {
    }
}