package com.medvault.controller;

import com.medvault.dto.AuthDtos.*;
import com.medvault.entity.AppUser;
import com.medvault.repository.AppUserRepository;
import com.medvault.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(AppUserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
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
}
