package auca.ac.rw.transportManagementSystem.service;

import java.util.*;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auca.ac.rw.transportManagementSystem.model.Bus;
import auca.ac.rw.transportManagementSystem.model.Driver;
import auca.ac.rw.transportManagementSystem.model.OtpPurpose;
import auca.ac.rw.transportManagementSystem.model.Role;
import auca.ac.rw.transportManagementSystem.model.Student;
import auca.ac.rw.transportManagementSystem.model.User;
import auca.ac.rw.transportManagementSystem.repository.BusRepository;
import auca.ac.rw.transportManagementSystem.repository.DriverRepository;
import auca.ac.rw.transportManagementSystem.repository.StudentRepository;
import auca.ac.rw.transportManagementSystem.repository.UserRepository;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Transactional
    public String saveDriver(Driver driver) {
        if (driver == null) {
            return "Driver cannot be null";
        }

        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            return "Driver name is required";
        }

        if (driver.getEmail() == null || driver.getEmail().trim().isEmpty()) {
            return "Driver email is required";
        }

        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            return "License number is required";
        }

        String normalizedName = driver.getName().trim();
        String normalizedEmail = driver.getEmail().trim().toLowerCase();
        String normalizedLicenseNumber = driver.getLicenseNumber().trim();
        String normalizedPhoneNumber = driver.getPhoneNumber() == null ? null : driver.getPhoneNumber().trim();
        if (normalizedPhoneNumber != null && normalizedPhoneNumber.isEmpty()) {
            normalizedPhoneNumber = null;
        }

        driver.setName(normalizedName);
        driver.setEmail(normalizedEmail);
        driver.setLicenseNumber(normalizedLicenseNumber);
        driver.setPhoneNumber(normalizedPhoneNumber);

        if (driverRepository.existsByLicenseNumberIgnoreCase(normalizedLicenseNumber)) {
            return "Driver with this license number already exists";
        }

        if (driverRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return "Driver with this email already exists";
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return "User with this email already exists";
        }

        try {
            // Create login account for driver
            User user = new User();
            user.setFullNames(normalizedName);
            user.setEmail(normalizedEmail);
            user.setPhoneNumber(normalizedPhoneNumber);
            user.setRole(Role.DRIVER);

            // Best-effort: inherit location from the currently authenticated user (e.g., admin creating the driver)
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
                    userRepository.findByEmailIgnoreCase(auth.getName())
                            .map(User::getLocation)
                            .ifPresent(user::setLocation);
                }
            } catch (Exception ignored) {
                // location is optional in the model; ignore any issues here
            }

            // Temporary random password; user will set a real one via OTP
            String tempPassword = UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);

            // Save driver record
            driverRepository.save(driver);

            // Send OTP for password setup (valid 5 minutes)
            otpService.sendPasswordOtp(normalizedEmail, OtpPurpose.PASSWORD_SETUP);
            return "Driver saved successfully";
        } catch (Exception e) {
            // Don't swallow runtime exceptions inside @Transactional, otherwise Spring will throw
            // UnexpectedRollbackException at commit time (causing a 500 even though we "handled" it).
            throw new IllegalArgumentException("Failed to save driver: " + e.getMessage(), e);
        }
    }

    public Optional<Driver> getDriverById(int id) {
        return driverRepository.findById(id);
    }

    public Optional<Driver> getDriverByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return driverRepository.findByLicenseNumberIgnoreCase(licenseNumber.trim());
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
            String newLicense = updatedDriver.getLicenseNumber().trim();
            if (driver.getLicenseNumber() == null || !driver.getLicenseNumber().equalsIgnoreCase(newLicense)) {
                if (driverRepository.existsByLicenseNumberIgnoreCase(newLicense)) {
                return "License number already exists";
                }
            }
            driver.setLicenseNumber(newLicense);
        }

        if (updatedDriver.getEmail() != null && !updatedDriver.getEmail().trim().isEmpty()) {
            String newEmail = updatedDriver.getEmail().trim().toLowerCase();
            if (driver.getEmail() == null || !driver.getEmail().equalsIgnoreCase(newEmail)) {
                if (driverRepository.existsByEmailIgnoreCase(newEmail)) {
                    return "Driver email already exists";
                }
                if (userRepository.existsByEmailIgnoreCase(newEmail)) {
                    return "User with this email already exists";
                }
                driver.setEmail(newEmail);
            }
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
        if (licenseNumber == null) return false;
        return driverRepository.existsByLicenseNumberIgnoreCase(licenseNumber.trim());
    }

    public Map<String, Object> getDriverStatistics(String driverEmail) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("busCount", 0);
        stats.put("studentCount", 0);
        stats.put("pickupPointCount", 0);
        stats.put("recentActivities", new ArrayList<>());

        if (driverEmail == null || driverEmail.trim().isEmpty()) {
            return stats;
        }

        try {
            String normalizedEmail = driverEmail.trim().toLowerCase();
            Optional<Driver> driverOpt = driverRepository.findByEmailIgnoreCase(normalizedEmail);
            if (driverOpt.isEmpty()) {
                System.out.println("DEBUG: Driver not found for email: " + normalizedEmail);
                return stats;
            }

            Driver driver = driverOpt.get();
            System.out.println("DEBUG: Found driver: " + driver.getName() + " (ID: " + driver.getDriverId() + ")");
            
            // Find bus assigned to this driver
            Optional<Bus> busOpt = busRepository.findByDriverDriverId(driver.getDriverId());
            
            // If not found via repository, try the bidirectional relationship
            // Note: This might be null due to lazy loading, so we rely on the repository query
            if (busOpt.isEmpty()) {
                // Try to find any bus with this driver
                List<Bus> allBuses = busRepository.findAll();
                for (Bus b : allBuses) {
                    if (b.getDriver() != null && b.getDriver().getDriverId() == driver.getDriverId()) {
                        busOpt = Optional.of(b);
                        break;
                    }
                }
            }
            
            if (busOpt.isPresent()) {
                Bus bus = busOpt.get();
                System.out.println("DEBUG: Found bus: " + bus.getPlateNumber() + " (ID: " + bus.getId() + ")");
                stats.put("busCount", 1);
                
                // Get students assigned to this bus
                List<Student> students = studentRepository.findByBusId(bus.getId());
                System.out.println("DEBUG: Found " + students.size() + " students for bus ID: " + bus.getId());
                stats.put("studentCount", students.size());
                
                // Get unique pickup points
                Set<String> pickupPoints = new HashSet<>();
                for (Student student : students) {
                    if (student.getPickUpPoint() != null && !student.getPickUpPoint().trim().isEmpty()) {
                        pickupPoints.add(student.getPickUpPoint().trim());
                    }
                }
                stats.put("pickupPointCount", pickupPoints.size());
                
                // Get recent activities (show student assignments and status)
                List<Map<String, Object>> activities = new ArrayList<>();
                for (Student student : students) {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("type", "Student Assignment");
                    String statusDesc = student.getStatus() != null ? student.getStatus().toString() : "ABSENT";
                    activity.put("description", student.getName() + " assigned - Status: " + statusDesc);
                    activity.put("time", new Date());
                    activities.add(activity);
                }
                // Sort by time descending and limit to 10
                if (!activities.isEmpty()) {
                    activities.sort((a, b) -> ((Date) b.get("time")).compareTo((Date) a.get("time")));
                    int limit = Math.min(10, activities.size());
                    stats.put("recentActivities", activities.subList(0, limit));
                }
            } else {
                System.out.println("DEBUG: No bus found for driver ID: " + driver.getDriverId());
            }
        } catch (Exception e) {
            System.err.println("ERROR in getDriverStatistics: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }
}