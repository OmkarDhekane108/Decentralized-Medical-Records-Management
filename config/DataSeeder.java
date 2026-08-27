package com.medvault.config;

import com.medvault.entity.AppUser;
import com.medvault.entity.AppointmentSlot;
import com.medvault.entity.Hospital;
import com.medvault.repository.AppUserRepository;
import com.medvault.repository.AppointmentSlotRepository;
import com.medvault.repository.HospitalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentSlotRepository slotRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int SLOT_DAYS = 7;
    private static final int SLOT_START_HOUR = 9;
    private static final int SLOT_END_HOUR = 16; // last slot starts 4 PM
    private static final int HOSPITAL_SLOT_CAPACITY = 3;
    private static final int DOCTOR_SLOT_CAPACITY = 1;

    public DataSeeder(AppUserRepository userRepository, HospitalRepository hospitalRepository,
                       AppointmentSlotRepository slotRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
        this.slotRepository = slotRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("patient1", "patient123", "Patient", "John Carter", null);
        seed("patient2", "patient123", "Patient", "Maira Khan", null);
        AppUser doctor1 = seed("doctor1", "doctor123", "Doctor", "Dr. Sarah Chen", "General Medicine");
        seed("admin1", "admin123", "Admin", "Alex Morgan", null);

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

        if (slotRepository.count() == 0) {
            List<Hospital> hospitals = hospitalRepository.findAll();
            for (Hospital h : hospitals) {
                generateSlotsForHospital(h);
            }
            if (doctor1 != null) {
                generateSlotsForDoctor(doctor1);
            }
        }
    }

    private void generateSlotsForHospital(Hospital hospital) {
        LocalDate today = LocalDate.now();
        for (int d = 0; d < SLOT_DAYS; d++) {
            LocalDate day = today.plusDays(d);
            for (int hour = SLOT_START_HOUR; hour <= SLOT_END_HOUR; hour++) {
                LocalDateTime slotTime = LocalDateTime.of(day, LocalTime.of(hour, 0));
                slotRepository.save(new AppointmentSlot(
                        hospital.getId(), hospital.getName(), null, null,
                        slotTime, HOSPITAL_SLOT_CAPACITY
                ));
            }
        }
    }

    private void generateSlotsForDoctor(AppUser doctor) {
        LocalDate today = LocalDate.now();
        for (int d = 0; d < SLOT_DAYS; d++) {
            LocalDate day = today.plusDays(d);
            for (int hour = SLOT_START_HOUR; hour <= SLOT_END_HOUR; hour++) {
                LocalDateTime slotTime = LocalDateTime.of(day, LocalTime.of(hour, 0));
                slotRepository.save(new AppointmentSlot(
                        null, null, doctor.getUsername(), doctor.getFullName(),
                        slotTime, DOCTOR_SLOT_CAPACITY
                ));
            }
        }
    }

    private AppUser seed(String username, String rawPassword, String role, String fullName, String specialization) {
        AppUser user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new AppUser(username, passwordEncoder.encode(rawPassword), role, fullName);
        }
        if (specialization != null) {
            user.setSpecialization(specialization);
        }
        return userRepository.save(user);
    }
}