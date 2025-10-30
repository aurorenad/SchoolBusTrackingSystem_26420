package auca.ac.rw.transportManagementSystem.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.ac.rw.transportManagementSystem.model.Gender;
import auca.ac.rw.transportManagementSystem.model.Role;
import auca.ac.rw.transportManagementSystem.model.User;
import auca.ac.rw.transportManagementSystem.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        String response = userService.saveUser(user);
        if (response.equals("User saved successfully")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/saveWithLocation", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveUserWithLocation(@RequestBody User user, 
                                                        @RequestParam String locationCode) {
        String response = userService.saveUserWithLocation(user, locationCode);
        if (response.equals("User saved successfully")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/assignLocation")
    public ResponseEntity<String> assignLocation(@RequestParam UUID userId, 
                                                  @RequestParam String locationCode) {
        String response = userService.assignLocation(userId, locationCode);
        if (response.equals("Location assigned successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<User>> getUsersByGender(@PathVariable Gender gender) {
        List<User> users = userService.getUsersByGender(gender);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/location/{locationCode}")
    public ResponseEntity<List<User>> getUsersByLocation(@PathVariable String locationCode) {
        List<User> users = userService.getUsersByLocation(locationCode);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestParam String id, 
                                          @RequestBody User user) {
        UUID userId = UUID.fromString(id);
        String response = userService.updateUser(userId, user);  
        if (response.equals("User updated successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String id) {
        UUID userId = UUID.fromString(id);
        String response = userService.deleteUser(userId);  
        if (response.equals("User deleted successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}