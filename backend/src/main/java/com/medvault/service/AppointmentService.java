package com.medvault.service;

import com.medvault.entity.Appointment;
import com.medvault.entity.Hospital;
import com.medvault.repository.AppointmentRepository;
import com.medvault.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final HospitalRepository hospitalRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               HospitalRepository hospitalRepository) {
        this.appointmentRepository = appointmentRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public Appointment book(String patientUsername, Long hospitalId, LocalDateTime time, String reason) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        if (hospital.getSlots() == null || hospital.getSlots() <= 0) {
            throw new RuntimeException("No slots available at this hospital");
        }

        hospital.setSlots(hospital.getSlots() - 1);
        hospitalRepository.save(hospital);

        Appointment appointment = new Appointment(
                patientUsername, hospital.getId(), hospital.getName(), time, reason
        );
        appointment.setStatus("CONFIRMED");
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getForPatient(String patientUsername) {
        return appointmentRepository.findByPatientUsernameOrderByAppointmentTimeDesc(patientUsername);
    }

    public Appointment cancel(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!"CANCELLED".equals(appointment.getStatus()) && appointment.getHospitalId() != null) {
            hospitalRepository.findById(appointment.getHospitalId()).ifPresent(h -> {
                h.setSlots((h.getSlots() == null ? 0 : h.getSlots()) + 1);
                hospitalRepository.save(h);
            });
        }

        appointment.setStatus("CANCELLED");
        return appointmentRepository.save(appointment);
    }
}