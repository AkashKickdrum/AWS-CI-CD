package com.caching.service;

import com.caching.exception.GeoCodingException;
import com.caching.exception.ReverseGeoCodingException;
import com.caching.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for handling geocoding and reverse geocoding operations.
 */
@Service
public class GeoCodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeoCodingService.class);
    private final RestTemplate restTemplate;

    public GeoCodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Geocodes an address into geographic coordinates.
     * @param address The address to geocode.
     * @return JSON string with latitude and longitude.
     */
    @Cacheable(value = Constants.GEOCODING_CACHE, key = Constants.FORWARD_GEOCODING_KEY,
            unless = "#address.toLowerCase().contains('goa')")
    public String geocode(String address) {
        logger.info("Processing geocoding request for address: {}", address);

        String url = buildForwardGeocodeUrl(address);
        Map<String, Object> responseMap = fetchApiResponse(url, address);

        List<?> data = extractData(responseMap, address);
        Map<String, Object> firstEntry = validateFirstEntry(data, address);

        Map<String, Object> result = new HashMap<>();
        result.put(Constants.LATITUDE, firstEntry.get(Constants.LATITUDE));
        result.put(Constants.LONGITUDE, firstEntry.get(Constants.LONGITUDE));
        logger.info("Geocoding successful for address: {}. Result: {}", address, result);

        return toJson(result);
    }

    /**
     * Reverse geocodes geographic coordinates into an address.
     * @param latitude The latitude component of the location.
     * @param longitude The longitude component of the location.
     * @return JSON string of the address corresponding to the coordinates.
     */
    @Cacheable(value = Constants.REVERSE_GEOCODING_CACHE, key = Constants.REVERSE_GEOCODING_KEY)
    public String reverseGeocode(double latitude, double longitude) {
        logger.info("Processing reverse geocoding request for Latitude: {}, Longitude: {}", latitude, longitude);

        String url = buildReverseGeocodeUrl(latitude, longitude);
        Map<String, Object> responseMap = fetchApiResponse(url, latitude + "," + longitude);

        List<?> data = extractData(responseMap, latitude + "," + longitude);
        Map<String, Object> firstEntry = validateFirstEntry(data, latitude + "," + longitude);

        String address = Optional.ofNullable((String) firstEntry.get("label"))
                .orElseThrow(() -> new ReverseGeoCodingException("Address not found in response.", HttpStatus.BAD_REQUEST));

        return extractNumericFromAddress(address);
    }

    /**
     * Builds the URL for the forward geocoding API request.
     * @param address The address to geocode.
     * @return The full URL to call the API.
     */
    private String buildForwardGeocodeUrl(String address) {
        return String.format("%s/forward?access_key=%s&query=%s",
                Constants.EXTERNAL_API_BASE_URL,
                Constants.API_ACCESS_KEY,
                address);
    }

    /**
     * Builds the URL for the reverse geocoding API request.
     * @param latitude Latitude for the location.
     * @param longitude Longitude for the location.
     * @return The full URL to call the API.
     */
    private String buildReverseGeocodeUrl(double latitude, double longitude) {
        return String.format("%s/reverse?access_key=%s&query=%f,%f",
                Constants.EXTERNAL_API_BASE_URL,
                Constants.API_ACCESS_KEY,
                latitude,
                longitude);
    }

    /**
     * Fetches the API response from a URL and converts it to a Map object.
     * @param url The URL to fetch the API response from.
     * @param query The query parameter for logging purposes.
     * @return A map representing the JSON response from the API.
     */
    private Map<String, Object> fetchApiResponse(String url, String query) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            logger.info("API response received for query: {}. Status: {}", query, response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("API returned non-200 status for query: {}", query);
                throw new GeoCodingException("Failed to fetch data for query: " + query, HttpStatus.BAD_REQUEST);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), Map.class);

        } catch (Exception ex) {
            logger.error("Error fetching API response for query: {}", query, ex);
            throw new GeoCodingException("Error fetching data for query: " + query, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extracts data from the API response.
     * @param responseMap The map containing the API response.
     * @param query The query parameter for logging purposes.
     * @return A list of data entries extracted from the response.
     */
    private List<?> extractData(Map<String, Object> responseMap, String query) {
        List<?> data = (List<?>) responseMap.get("data");
        if (data == null || data.isEmpty()) {
            logger.error("No data found for query: {}", query);
            throw new GeoCodingException("No data found for query: " + query, HttpStatus.BAD_REQUEST);
        }
        return data;
    }

    /**
     * Validates the first entry in the list of data to ensure it contains required fields.
     * @param data The list of data entries.
     * @param query The query parameter for logging purposes.
     * @return A map representing the first valid data entry.
     */
    private Map<String, Object> validateFirstEntry(List<?> data, String query) {
        Map<String, Object> firstEntry = (Map<String, Object>) data.get(0);
        if (!firstEntry.containsKey(Constants.LATITUDE) || !firstEntry.containsKey(Constants.LONGITUDE)) {
            logger.error("Missing latitude or longitude in response for query: {}", query);
            throw new GeoCodingException("Missing latitude or longitude for query: " + query, HttpStatus.BAD_REQUEST);
        }
        return firstEntry;
    }

    /**
     * Converts a map to a JSON string.
     * @param map The map to serialize.
     * @return The JSON string representation of the map.
     */
    private String toJson(Map<String, Object> map) {
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (Exception ex) {
            logger.error("Error serializing map to JSON: {}", map, ex);
            throw new GeoCodingException("Error processing response.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extracts numeric values from an address string.
     * @param address The address string to analyze.
     * @return The numeric part of the address, if present.
     */
    private String extractNumericFromAddress(String address) {
        return Optional.ofNullable(address.split(" "))
                .flatMap(parts -> List.of(parts).stream()
                        .filter(part -> part.matches("\\d+"))
                        .findFirst())
                .orElseThrow(() -> new ReverseGeoCodingException("No numeric value found in address.", HttpStatus.BAD_REQUEST));
    }
}
