package com.medvault.controller;

import com.medvault.dto.AppointmentDtos.*;
import com.medvault.entity.Hospital;
import com.medvault.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public ResponseEntity<List<Hospital>> getAll(
            @RequestParam(required = false) String specialization) {
        if (specialization != null && !specialization.isBlank()) {
            return ResponseEntity.ok(hospitalService.getBySpecialization(specialization));
        }
        return ResponseEntity.ok(hospitalService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody HospitalRequest req) {
        if (req.name == null || req.name.isBlank()) {
            return ResponseEntity.status(400).body(new ErrorResponse("Hospital name is required."));
        }
        Hospital hospital = new Hospital(
                req.name, req.lat, req.lng, req.specialization,
                req.slots == null ? 0 : req.slots, req.address, req.phone
        );
        return ResponseEntity.ok(hospitalService.add(hospital));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        hospitalService.delete(id);
        return ResponseEntity.ok().build();
    }
}