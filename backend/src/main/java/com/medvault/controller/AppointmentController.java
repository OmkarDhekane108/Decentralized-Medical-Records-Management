package com.medvault.controller;

import com.medvault.dto.AppointmentDtos.*;
import com.medvault.entity.Appointment;
import com.medvault.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> book(@RequestBody BookRequest req) {
        if (req.patientUsername == null || req.patientUsername.isBlank()) {
            return ResponseEntity.status(400).body(new ErrorResponse("patientUsername is required."));
        }
        if (req.hospitalId == null) {
            return ResponseEntity.status(400).body(new ErrorResponse("hospitalId is required."));
        }
        if (req.appointmentTime == null || req.appointmentTime.isBlank()) {
            return ResponseEntity.status(400).body(new ErrorResponse("appointmentTime is required."));
        }

        try {
            LocalDateTime time = LocalDateTime.parse(req.appointmentTime);
            Appointment appointment = appointmentService.book(
                    req.patientUsername, req.hospitalId, time, req.reason);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/patient/{username}")
    public ResponseEntity<List<Appointment>> getForPatient(@PathVariable String username) {
        return ResponseEntity.ok(appointmentService.getForPatient(username));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(appointmentService.cancel(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }
}
