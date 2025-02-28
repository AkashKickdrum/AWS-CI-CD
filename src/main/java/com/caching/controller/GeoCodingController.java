package com.caching.controller;

import com.caching.service.GeoCodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for geocoding and reverse geocoding operations.
 */
@RestController
@RequestMapping("")
public class GeoCodingController {

    private final GeoCodingService geoCodingService;
    public GeoCodingController(GeoCodingService geoCodingService) {
        this.geoCodingService = geoCodingService;
    }
    @GetMapping("/geocoding")
    public ResponseEntity<String> geocode(@RequestParam String address) {
        String result = geoCodingService.geocode(address);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/reverse-geocoding")
    public ResponseEntity<String> reverseGeocode(@RequestParam("latitude") double latitude,
                                                 @RequestParam("longitude") double longitude) {
        String result = geoCodingService.reverseGeocode(latitude, longitude);
        return ResponseEntity.ok(result);
    }
}
