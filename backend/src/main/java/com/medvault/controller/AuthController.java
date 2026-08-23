package com.medvault.controller;

import com.medvault.dto.AuthDtos.*;
import com.medvault.entity.AppUser;
import com.medvault.repository.AppUserRepository;
import com.medvault.service.CredentialGeneratorService;
import com.medvault.service.MailService;
import com.medvault.service.OtpService;
import com.medvault.service.SmsService;
import com.medvault.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final OtpService otpService;
    private final MailService mailService;
    private final SmsService smsService;
    private final CredentialGeneratorService credentialGenerator;

    private static final String UPLOAD_DIR = "uploads/profile-pictures";

    public AuthController(AppUserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TokenService tokenService,
                           OtpService otpService,
                           MailService mailService,
                           SmsService smsService,
                           CredentialGeneratorService credentialGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.otpService = otpService;
        this.mailService = mailService;
        this.smsService = smsService;
        this.credentialGenerator = credentialGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<AppUser> found = userRepository.findByUsername(req.username);

        if (found.isEmpty()
                || !found.get().isActive()
                || !found.get().getRole().equalsIgnoreCase(req.role)
                || !passwordEncoder.matches(req.password, found.get().getPasswordHash())) {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid username, password, or role."));
        }

        AppUser user = found.get();
        String token = tokenService.issueToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole(), user.getFullName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        tokenService.revoke(token);
        return ResponseEntity.ok().build();
    }

    // Kept for backward compatibility (JSON register with a client-chosen username/password)
    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerJson(@RequestBody RegisterRequest req) {
        if (req.username == null || req.username.isBlank()) {
            return ResponseEntity.status(400).body(new ErrorResponse("Username is required."));
        }
        if (req.password == null || req.password.length() < 6) {
            return ResponseEntity.status(400).body(new ErrorResponse("Password must be at least 6 characters."));
        }
        if (userRepository.findByUsername(req.username).isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("Username already exists. Please choose another."));
        }

        AppUser user = new AppUser(req.username, passwordEncoder.encode(req.password), "PATIENT", req.fullName);
        user.setAge(req.age);
        user.setWeight(req.weight);
        user.setMobile(req.mobile);
        user.setEmail(req.email);
        userRepository.save(user);

        String token = tokenService.issueToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole(), user.getFullName()));
    }

    // ================== OTP: MOBILE ==================

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendMobileOtp(@RequestBody SendMobileOtpRequest req) {
        if (req.mobile == null || req.mobile.isBlank()) {
            return ResponseEntity.status(400).body(new ErrorResponse("Mobile number is required."));
        }
        String otp = otpService.generateOtp("mobile:" + req.mobile);
        try {
            if (req.useWhatsapp) {
                smsService.sendOtpWhatsapp(req.mobile, otp);
            } else {
                smsService.sendOtpSms(req.mobile, otp);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to send OTP: " + e.getMessage()));
        }
        return ResponseEntity.ok(new SimpleMessageResponse("OTP sent to mobile number."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyMobileOtp(@RequestBody VerifyMobileOtpRequest req) {
        if (req.mobile == null || req.otp == null) {
            return ResponseEntity.status(400).body(new ErrorResponse("Mobile and OTP are required."));
        }
        boolean matched = otpService.verifyOtp("mobile:" + req.mobile, req.otp);
        if (!matched) {
            return ResponseEntity.status(400).body(new ErrorResponse("OTP is not matched. Please enter the correct OTP."));
        }
        return ResponseEntity.ok(new SimpleMessageResponse("Mobile number verified."));
    }

    // ================== OTP: EMAIL ==================

    @PostMapping("/send-email-otp")
    public ResponseEntity<?> sendEmailOtp(@RequestBody SendEmailOtpRequest req) {
        if (req.email == null || req.email.isBlank()) {
            return ResponseEntity.status(400).body(new ErrorResponse("Email is required."));
        }
        String otp = otpService.generateOtp("email:" + req.email);
        try {
            mailService.sendOtpEmail(req.email, otp);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to send OTP email: " + e.getMessage()));
        }
        return ResponseEntity.ok(new SimpleMessageResponse("OTP sent to email."));
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody VerifyEmailOtpRequest req) {
        if (req.email == null || req.otp == null) {
            return ResponseEntity.status(400).body(new ErrorResponse("Email and OTP are required."));
        }
        boolean matched = otpService.verifyOtp("email:" + req.email, req.otp);
        if (!matched) {
            return ResponseEntity.status(400).body(new ErrorResponse("OTP is not matched. Please enter the correct OTP."));
        }
        return ResponseEntity.ok(new SimpleMessageResponse("Email verified."));
    }

    // ================== FINAL REGISTRATION (multipart, auto-generated credentials) ==================

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> registerWithPhoto(
            @RequestParam String fullName,
            @RequestParam(required = false) Double weight,
            @RequestParam String mobile,
            @RequestParam String email,
            @RequestParam(defaultValue = "PATIENT") String role,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        // Server-side re-check: don't trust the frontend's "verified" state alone
        if (!otpService.isVerified("mobile:" + mobile)) {
            return ResponseEntity.status(400).body(new ErrorResponse("Mobile number is not OTP-verified."));
        }
        if (!otpService.isVerified("email:" + email)) {
            return ResponseEntity.status(400).body(new ErrorResponse("Email is not OTP-verified."));
        }
        if (userRepository.findByMobile(mobile).isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("Mobile number already registered."));
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("Email already registered."));
        }

        String username = credentialGenerator.generateUsername(fullName);
        while (userRepository.findByUsername(username).isPresent()) {
            username = credentialGenerator.generateUsername(fullName); // regenerate on rare collision
        }
        String plainPassword = credentialGenerator.generatePassword();

        AppUser user = new AppUser(username, passwordEncoder.encode(plainPassword), role.toUpperCase(), fullName);
        user.setWeight(weight);
        user.setMobile(mobile);
        user.setEmail(email);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                String savedPath = saveProfilePicture(profilePicture);
                user.setProfilePicturePath(savedPath);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(new ErrorResponse("Failed to save profile picture: " + e.getMessage()));
            }
        }

        userRepository.save(user);

        // Consume the OTP-verified flags so they can't be reused for another registration
        otpService.clearVerified("mobile:" + mobile);
        otpService.clearVerified("email:" + email);

        // Send generated credentials to the user
        try {
            mailService.sendCredentialsEmail(email, username, plainPassword);
        } catch (Exception ignored) {
            // registration already succeeded; email failure shouldn't block the response
        }
        try {
            smsService.sendOtpSms(mobile, "Your MedChain username is " + username + " and password is " + plainPassword);
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(new RegisterResultResponse(
                "Registration successful. Credentials sent to your mobile and email.",
                username, plainPassword, user.getRole()
        ));
    }

    private String saveProfilePicture(MultipartFile file) throws IOException {
        Path dir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String extension = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + extension;
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target);
        return UPLOAD_DIR + "/" + filename;
    }
}
