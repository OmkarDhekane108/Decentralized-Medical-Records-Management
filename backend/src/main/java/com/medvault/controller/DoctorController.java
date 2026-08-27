package com.medvault.controller;

import com.medvault.dto.AppointmentDtos.DoctorResponse;
import com.medvault.repository.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final AppUserRepository userRepository;

    public DoctorController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAll() {
        List<DoctorResponse> doctors = userRepository.findByRole("DOCTOR").stream()
                .map(u -> new DoctorResponse(u.getUsername(), u.getFullName(), u.getSpecialization()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
}