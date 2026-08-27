package com.medvault.controller;

import com.medvault.entity.AppointmentSlot;
import com.medvault.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final AppointmentService appointmentService;

    public SlotController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<AppointmentSlot>> getForHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(appointmentService.getSlotsForHospital(hospitalId));
    }

    @GetMapping("/doctor/{username}")
    public ResponseEntity<List<AppointmentSlot>> getForDoctor(@PathVariable String username) {
        return ResponseEntity.ok(appointmentService.getSlotsForDoctor(username));
    }
}