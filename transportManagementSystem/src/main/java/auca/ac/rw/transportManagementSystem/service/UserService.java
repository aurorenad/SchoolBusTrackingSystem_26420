package auca.ac.rw.transportManagementSystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.ac.rw.transportManagementSystem.model.Gender;
import auca.ac.rw.transportManagementSystem.model.Location;
import auca.ac.rw.transportManagementSystem.model.Role;
import auca.ac.rw.transportManagementSystem.model.User;
import auca.ac.rw.transportManagementSystem.repository.LocationRepository;
import auca.ac.rw.transportManagementSystem.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

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

        if (userRepository.existsByEmail(user.getEmail())) {
            return "User with this email already exists";
        }

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
        return userRepository.findByEmail(email);
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
            if (!user.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
                return "Email already exists";
            }
            user.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
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
        return userRepository.existsByEmail(email);
    }
}