package com.medvault.config;

import com.medvault.entity.AppUser;
import com.medvault.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("patient1", "patient123", "Patient", "John Carter");
        seed("patient2", "patient123", "Patient", "Maira Khan");
        seed("doctor1", "doctor123", "Doctor", "Dr. Sarah Chen");
        seed("admin1", "admin123", "Admin", "Alex Morgan");
    }

    private void seed(String username, String rawPassword, String role, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) return;
        userRepository.save(new AppUser(username, passwordEncoder.encode(rawPassword), role, fullName));
    }
}
