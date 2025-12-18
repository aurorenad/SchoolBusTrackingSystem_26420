package auca.ac.rw.transportManagementSystem.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auca.ac.rw.transportManagementSystem.model.OtpPurpose;
import auca.ac.rw.transportManagementSystem.model.OtpToken;
import auca.ac.rw.transportManagementSystem.model.User;
import auca.ac.rw.transportManagementSystem.repository.OtpTokenRepository;
import auca.ac.rw.transportManagementSystem.repository.UserRepository;

@Service
public class OtpService {
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final SecureRandom RNG = new SecureRandom();

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Transactional
    public void sendPasswordOtp(String email, OtpPurpose purpose) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = generateSixDigitOtp();

        OtpToken token = new OtpToken();
        token.setEmail(normalizedEmail);
        token.setPurpose(purpose);
        token.setCode(otp);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(OTP_TTL));
        token.setUsedAt(null);
        otpTokenRepository.save(token);

        String subject = purpose == OtpPurpose.PASSWORD_SETUP ? "Set your password" : "Reset your password";
        
        // Build the appropriate link based on purpose
        String linkPath = purpose == OtpPurpose.PASSWORD_SETUP ? "/set-password" : "/forgot-password";
        String setupLink = frontendUrl + linkPath + "?email=" + java.net.URLEncoder.encode(normalizedEmail, java.nio.charset.StandardCharsets.UTF_8);
        
        // Create email body with clickable link
        String body = "Hello " + user.getFullNames() + ",\n\n"
                + "Your OTP code is: " + otp + "\n"
                + "It expires in 5 minutes.\n\n"
                + "Click the link below to " + (purpose == OtpPurpose.PASSWORD_SETUP ? "set" : "reset") + " your password:\n"
                + setupLink + "\n\n"
                + "Or manually enter the OTP code in the app.\n\n"
                + "If you did not request this, please ignore this email.\n";

        emailService.sendPlainText(normalizedEmail, subject, body);
    }

    @Transactional
    public void confirmPasswordOtpAndSetPassword(String email, String otp, String newPassword, OtpPurpose purpose) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (otp == null || otp.trim().isEmpty()) {
            throw new IllegalArgumentException("OTP is required");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }
        if (newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        String normalizedEmail = email.trim().toLowerCase();
        String normalizedOtp = otp.trim();

        OtpToken token = otpTokenRepository
                .findTopByEmailAndPurposeAndCodeAndUsedAtIsNullOrderByCreatedAtDesc(normalizedEmail, purpose, normalizedOtp)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("OTP expired");
        }

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);

        token.setUsedAt(Instant.now());
        otpTokenRepository.save(token);
    }

    private String generateSixDigitOtp() {
        int value = RNG.nextInt(1_000_000); // 0..999999
        return String.format("%06d", value);
    }
}

