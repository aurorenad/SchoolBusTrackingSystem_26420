package auca.ac.rw.transportManagementSystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.ac.rw.transportManagementSystem.model.Driver;
import auca.ac.rw.transportManagementSystem.repository.DriverRepository;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public String saveDriver(Driver driver) {
        if (driver == null) {
            return "Driver cannot be null";
        }

        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            return "Driver name is required";
        }

        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            return "License number is required";
        }

        if (driverRepository.existsByLicenseNumber(driver.getLicenseNumber())) {
            return "Driver with this license number already exists";
        }

        try {
            driverRepository.save(driver);
            return "Driver saved successfully";
        } catch (Exception e) {
            return "Failed to save driver: " + e.getMessage();
        }
    }

    public Optional<Driver> getDriverById(int id) {
        return driverRepository.findById(id);
    }

    public Optional<Driver> getDriverByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return driverRepository.findByLicenseNumber(licenseNumber);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Driver> getDriversByExperience(int minYears) {
        return driverRepository.findByExperienceYearsGreaterThanEqual(minYears);
    }

    public List<Driver> searchDriversByName(String name) {
        return driverRepository.findByNameContainingIgnoreCase(name);
    }

    public String updateDriver(int id, Driver updatedDriver) {
        Optional<Driver> existingDriverOptional = driverRepository.findById(id);
        if (!existingDriverOptional.isPresent()) {
            return "Driver not found";
        }

        Driver driver = existingDriverOptional.get();

        if (updatedDriver.getName() != null && !updatedDriver.getName().trim().isEmpty()) {
            driver.setName(updatedDriver.getName());
        }

        if (updatedDriver.getLicenseNumber() != null && !updatedDriver.getLicenseNumber().trim().isEmpty()) {
            if (!driver.getLicenseNumber().equals(updatedDriver.getLicenseNumber()) &&
                driverRepository.existsByLicenseNumber(updatedDriver.getLicenseNumber())) {
                return "License number already exists";
            }
            driver.setLicenseNumber(updatedDriver.getLicenseNumber());
        }

        if (updatedDriver.getPhoneNumber() != null && !updatedDriver.getPhoneNumber().trim().isEmpty()) {
            driver.setPhoneNumber(updatedDriver.getPhoneNumber());
        }

        if (updatedDriver.getExperienceYears() > 0) {
            driver.setExperienceYears(updatedDriver.getExperienceYears());
        }

        try {
            driverRepository.save(driver);
            return "Driver updated successfully";
        } catch (Exception e) {
            return "Failed to update driver: " + e.getMessage();
        }
    }

    public String deleteDriver(int id) {
        Optional<Driver> driverOptional = driverRepository.findById(id);
        if (!driverOptional.isPresent()) {
            return "Driver not found";
        }

        Driver driver = driverOptional.get();
        if (driver.getBus() != null) {
            return "Cannot delete driver assigned to a bus";
        }

        try {
            driverRepository.deleteById(id);
            return "Driver deleted successfully";
        } catch (Exception e) {
            return "Failed to delete driver: " + e.getMessage();
        }
    }

    public boolean existsByLicenseNumber(String licenseNumber) {
        return driverRepository.existsByLicenseNumber(licenseNumber);
    }
}