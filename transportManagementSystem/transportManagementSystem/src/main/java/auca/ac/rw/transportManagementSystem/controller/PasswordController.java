package auca.ac.rw.transportManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import auca.ac.rw.transportManagementSystem.dto.PasswordOtpConfirmRequest;
import auca.ac.rw.transportManagementSystem.dto.PasswordOtpRequest;
import auca.ac.rw.transportManagementSystem.model.OtpPurpose;
import auca.ac.rw.transportManagementSystem.service.OtpService;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/otp/request")
    public ResponseEntity<?> requestOtp(@RequestBody PasswordOtpRequest request) {
        try {
            OtpPurpose purpose = request.getPurpose() != null ? request.getPurpose() : OtpPurpose.PASSWORD_RESET;
            otpService.sendPasswordOtp(request.getEmail(), purpose);
            return ResponseEntity.ok("OTP sent successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
        }
    }

    @PostMapping("/otp/confirm")
    public ResponseEntity<?> confirmOtp(@RequestBody PasswordOtpConfirmRequest request) {
        try {
            OtpPurpose purpose = request.getPurpose() != null ? request.getPurpose() : OtpPurpose.PASSWORD_RESET;
            otpService.confirmPasswordOtpAndSetPassword(request.getEmail(), request.getOtp(), request.getNewPassword(), purpose);
            return ResponseEntity.ok("Password updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password");
        }
    }
}

