package auca.ac.rw.transportManagementSystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import auca.ac.rw.transportManagementSystem.dto.LocationHierarchyResponse;
import auca.ac.rw.transportManagementSystem.dto.UserLocationResponse;
import auca.ac.rw.transportManagementSystem.model.Elocation;
import auca.ac.rw.transportManagementSystem.model.Gender;
import auca.ac.rw.transportManagementSystem.model.Location;
import auca.ac.rw.transportManagementSystem.model.Role;
import auca.ac.rw.transportManagementSystem.model.User;
import auca.ac.rw.transportManagementSystem.repository.LocationRepository;
import auca.ac.rw.transportManagementSystem.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveUser(User user) {
        if (user == null) {
            return "User cannot be null";
        }

        if (user.getFullNames() == null || user.getFullNames().trim().isEmpty()) {
            return "Full names are required";
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "Email is required";
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "Password is required";
        }

        String normalizedEmail = user.getEmail().trim().toLowerCase();
        user.setEmail(normalizedEmail);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return "User with this email already exists";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
            return "User saved successfully";
        } catch (Exception e) {
            return "Failed to save user: " + e.getMessage();
        }
    }

    public String saveUserWithLocation(User user, String locationCode) {
       
        if (locationCode != null && !locationCode.trim().isEmpty()) {
            Optional<Location> locationOptional = locationRepository.findByCode(locationCode);
            if (!locationOptional.isPresent()) {
                return "Location not found";
            }
            user.setLocation(locationOptional.get());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
            return "User saved successfully";
        } catch (Exception e) {
            return "Failed to save user: " + e.getMessage();
        }
    }

    public String assignLocation(UUID userId, String locationCode) {
        if (locationCode == null || locationCode.trim().isEmpty()) {
            return "Location code is required";
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return "User not found";
        }

        Optional<Location> locationOptional = locationRepository.findByCode(locationCode);
        if (!locationOptional.isPresent()) {
            return "Location not found";
        }

        User user = userOptional.get();
        user.setLocation(locationOptional.get());

        try {
            userRepository.save(user);
            return "Location assigned successfully";
        } catch (Exception e) {
            return "Failed to assign location: " + e.getMessage();
        }
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(email.trim());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByGender(Gender gender) {
        return userRepository.findByGender(gender);
    }

    public List<User> getUsersByLocation(String locationCode) {
        return userRepository.findByLocationCode(locationCode);
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByFullNamesContainingIgnoreCase(name);
    }

    public List<UserLocationResponse> searchUsersByNameWithLocation(String name) {
        List<User> users = userRepository.findByFullNamesContainingIgnoreCase(name);
        return users.stream()
                .map(this::toUserLocationResponse)
                .collect(Collectors.toList());
    }

    private UserLocationResponse toUserLocationResponse(User user) {
        UserLocationResponse response = new UserLocationResponse();
        response.setUserId(user.getUserId());
        response.setFullNames(user.getFullNames());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setGender(user.getGender());
        response.setRole(user.getRole());

        response.setLocation(buildLocationHierarchy(user.getLocation()));
        return response;
    }

    private LocationHierarchyResponse buildLocationHierarchy(Location location) {
        LocationHierarchyResponse hierarchy = new LocationHierarchyResponse();

        Location current = location;
        while (current != null) {
            Elocation type = current.getType();
            if (type != null) {
                switch (type) {
                    case PROVINCE:
                        hierarchy.setProvince(current.getName());
                        break;
                    case DISTRICT:
                        hierarchy.setDistrict(current.getName());
                        break;
                    case SECTOR:
                        hierarchy.setSector(current.getName());
                        break;
                    case CELL:
                        hierarchy.setCell(current.getName());
                        break;
                    case VILLAGE:
                        hierarchy.setVillage(current.getName());
                        break;
                    default:
                        break;
                }
            }
            current = current.getParent();
        }

        return hierarchy;
    }

    public String updateUser(UUID id, User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (!existingUserOptional.isPresent()) {
            return "User not found";
        }

        User user = existingUserOptional.get();

        if (updatedUser.getFullNames() != null && !updatedUser.getFullNames().trim().isEmpty()) {
            user.setFullNames(updatedUser.getFullNames());
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
            String newEmail = updatedUser.getEmail().trim().toLowerCase();
            if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(newEmail)) {
                if (userRepository.existsByEmailIgnoreCase(newEmail)) {
                return "Email already exists";
                }
            }
            user.setEmail(newEmail);
        }

        if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (updatedUser.getGender() != null) {
            user.setGender(updatedUser.getGender());
        }

        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }

        try {
            userRepository.save(user);
            return "User updated successfully";
        } catch (Exception e) {
            return "Failed to update user: " + e.getMessage();
        }
    }

     public String deleteUser(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return "User not found";
        }

        try {
            userRepository.deleteById(id);
            return "User deleted successfully";
        } catch (Exception e) {
            return "Failed to delete user: " + e.getMessage();
        }
    }

    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return userRepository.existsByEmailIgnoreCase(email.trim());
    }

    // Get users by province code with pagination and sorting
    public Page<User> getUsersByProvinceCode(String provinceCode, int page, int size, String sortBy, String sortDir) {
        if (provinceCode == null || provinceCode.trim().isEmpty()) {
            return Page.empty();
        }
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByProvinceCode(provinceCode, pageable);
    }

    // Get users by province code without pagination
    public List<User> getUsersByProvinceCode(String provinceCode) {
        if (provinceCode == null || provinceCode.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findByProvinceCode(provinceCode);
    }

    // Get users by province name with pagination and sorting
    public Page<User> getUsersByProvinceName(String provinceName, int page, int size, String sortBy, String sortDir) {
        if (provinceName == null || provinceName.trim().isEmpty()) {
            return Page.empty();
        }
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByProvinceName(provinceName, pageable);
    }

    // Get users by province name without pagination
    public List<User> getUsersByProvinceName(String provinceName) {
        if (provinceName == null || provinceName.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findByProvinceName(provinceName);
    }

    // Get province from a user (traverse location hierarchy)
    public Optional<Location> getProvinceFromUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent() || userOptional.get().getLocation() == null) {
            return Optional.empty();
        }
        
        Location location = userOptional.get().getLocation();
        return locationService.findProvinceFromLocation(location);
    }

    // Get province from a user by user email
    public Optional<Location> getProvinceFromUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (!userOptional.isPresent() || userOptional.get().getLocation() == null) {
            return Optional.empty();
        }
        
        Location location = userOptional.get().getLocation();
        return locationService.findProvinceFromLocation(location);
    }

    // Check if users exist for a province code
    public boolean existsByProvinceCode(String provinceCode) {
        if (provinceCode == null || provinceCode.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByProvinceCode(provinceCode);
    }

    // Check if users exist for a province name
    public boolean existsByProvinceName(String provinceName) {
        if (provinceName == null || provinceName.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByProvinceName(provinceName);
    }

    // Get all users with pagination and sorting
    public Page<User> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    // Get users by role with pagination and sorting
    public Page<User> getUsersByRole(Role role, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByRole(role, pageable);
    }

    // Get users by gender with pagination and sorting
    public Page<User> getUsersByGender(Gender gender, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByGender(gender, pageable);
    }

    // Search users by name with pagination and sorting
    public Page<User> searchUsersByName(String name, int page, int size, String sortBy, String sortDir) {
        if (name == null || name.trim().isEmpty()) {
            return Page.empty();
        }
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByFullNamesContainingIgnoreCase(name, pageable);
    }
}