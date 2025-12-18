package auca.ac.rw.transportManagementSystem.controller;

import auca.ac.rw.transportManagementSystem.config.JwtService;
import auca.ac.rw.transportManagementSystem.dto.AuthRequest;
import auca.ac.rw.transportManagementSystem.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.getUsername());
                return ResponseEntity.ok(new AuthResponse(token));
            } else {
                return ResponseEntity.status(401).body("Authentication failed");
            }
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during authentication");
        }
    }
}
