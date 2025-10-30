package auca.ac.rw.transportManagementSystem.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.ac.rw.transportManagementSystem.model.Driver;
import auca.ac.rw.transportManagementSystem.service.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveDriver(@RequestBody Driver driver) {
        String response = driverService.saveDriver(driver);
        if (response.equals("Driver saved successfully")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable int id) {
        Optional<Driver> driver = driverService.getDriverById(id);
        if (driver.isPresent()) {
            return ResponseEntity.ok(driver.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
        }
    }

    @GetMapping("/license/{licenseNumber}")
    public ResponseEntity<?> getDriverByLicenseNumber(@PathVariable String licenseNumber) {
        Optional<Driver> driver = driverService.getDriverByLicenseNumber(licenseNumber);
        if (driver.isPresent()) {
            return ResponseEntity.ok(driver.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/experience/{minYears}")
    public ResponseEntity<List<Driver>> getDriversByExperience(@PathVariable int minYears) {
        List<Driver> drivers = driverService.getDriversByExperience(minYears);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Driver>> searchDrivers(@RequestParam String name) {
        List<Driver> drivers = driverService.searchDriversByName(name);
        return ResponseEntity.ok(drivers);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateDriver(@PathVariable int id, 
                                                @RequestBody Driver driver) {
        String response = driverService.updateDriver(id, driver);
        if (response.equals("Driver updated successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable int id) {
        String response = driverService.deleteDriver(id);
        if (response.equals("Driver deleted successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/exists/{licenseNumber}")
    public ResponseEntity<Boolean> checkExists(@PathVariable String licenseNumber) {
        boolean exists = driverService.existsByLicenseNumber(licenseNumber);
        return ResponseEntity.ok(exists);
    }
}