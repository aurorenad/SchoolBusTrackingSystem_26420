package auca.ac.rw.transportManagementSystem.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.ac.rw.transportManagementSystem.model.Elocation;
import auca.ac.rw.transportManagementSystem.model.Location;
import auca.ac.rw.transportManagementSystem.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveProvince(@RequestBody Location location) {
        String response = locationService.saveProvince(location);
        if (response.equals("Province saved successfully")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/saveChild", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveChildren(@RequestBody Location location, 
                                                @RequestParam String parentCode) {
        String response = locationService.saveChildren(parentCode, location);
        if (response.contains("successfully")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getLocation")
    public ResponseEntity<?> getLocation(@RequestParam String code) {
        Optional<Location> location = locationService.getLocation(code);
        if (location.isPresent()) {
            return ResponseEntity.ok(location.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found");
        }
    }

    @GetMapping("/getProvinceNameBySector")
    public ResponseEntity<String> getProvinceNameBySector(@RequestParam String code) {
        String response = locationService.getProvinceBySector(code);
        if (response.equals("No location found") || response.contains("no parent") || response.contains("no province")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByType(@PathVariable Elocation type) {
        List<Location> locations = locationService.getLocationsByType(type);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/roots")
    public ResponseEntity<List<Location>> getRootLocations() {
        List<Location> locations = locationService.getRootLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{code}/children")
    public ResponseEntity<List<Location>> getChildren(@PathVariable String code) {
        List<Location> children = locationService.getChildrenByParentCode(code);
        return ResponseEntity.ok(children);
    }

    @PutMapping(value = "/{code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateLocation(@PathVariable String code, 
                                                  @RequestBody Location location) {
        String response = locationService.updateLocation(code, location);
        if (response.equals("Location updated successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<String> deleteLocation(@PathVariable String code) {
        String response = locationService.deleteLocation(code);
        if (response.equals("Location deleted successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Location>> searchLocations(@RequestParam String name) {
        List<Location> locations = locationService.searchLocationsByName(name);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/exists/{code}")
    public ResponseEntity<Boolean> checkExists(@PathVariable String code) {
        boolean exists = locationService.existsByCode(code);
        return ResponseEntity.ok(exists);
    }
}