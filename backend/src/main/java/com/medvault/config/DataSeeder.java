package com.medvault.config;

import com.medvault.entity.AppUser;
import com.medvault.entity.Hospital;
import com.medvault.repository.AppUserRepository;
import com.medvault.repository.HospitalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AppUserRepository userRepository, HospitalRepository hospitalRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("patient1", "patient123", "Patient", "John Carter");
        seed("patient2", "patient123", "Patient", "Maira Khan");
        seed("doctor1", "doctor123", "Doctor", "Dr. Sarah Chen");
        seed("admin1", "admin123", "Admin", "Alex Morgan");

        if (hospitalRepository.count() == 0) {
            hospitalRepository.save(new Hospital("City Multispecialty Hospital",
                    18.5204, 73.8567, "Cardiology", 5, "", ""));
            hospitalRepository.save(new Hospital("Sunrise Ortho Center",
                    18.5300, 73.8400, "Orthopedics", 3, "", ""));
            hospitalRepository.save(new Hospital("Nova Neuro & Spine Institute",
                    18.5108, 73.8291, "Neurology", 2, "", ""));
            hospitalRepository.save(new Hospital("Dr. Sachin Deshmukh ENT & Maternity Hospital",
                    18.1780, 76.0430, "ENT (Ear, Nose & Throat)", 4,
                    "Near Chh. Shivaji High School, Tambari Vibhag, Dharashiv (Osmanabad), Maharashtra 413501",
                    "094051 44681"));
        }
    }

    private void seed(String username, String rawPassword, String role, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) return;
        userRepository.save(new AppUser(username, passwordEncoder.encode(rawPassword), role, fullName));
    }
}