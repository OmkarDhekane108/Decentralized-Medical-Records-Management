package com.medvault.service;

import com.medvault.entity.Appointment;
import com.medvault.entity.AppointmentSlot;
import com.medvault.repository.AppointmentRepository;
import com.medvault.repository.AppointmentSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository slotRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               AppointmentSlotRepository slotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
    }

    public Appointment book(String patientUsername, Long slotId, String reason) {
        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getRemaining() <= 0) {
            throw new RuntimeException("This slot is fully booked. Please choose another.");
        }

        slot.setBooked(slot.getBooked() + 1);
        slotRepository.save(slot);

        Appointment appointment = new Appointment(
                patientUsername, slot.getId(), slot.getHospitalId(), slot.getHospitalName(),
                slot.getDoctorUsername(), slot.getDoctorName(),
                slot.getSlotTime(), reason
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

        if (!"CANCELLED".equals(appointment.getStatus()) && appointment.getSlotId() != null) {
            slotRepository.findById(appointment.getSlotId()).ifPresent(slot -> {
                slot.setBooked(Math.max(0, slot.getBooked() - 1));
                slotRepository.save(slot);
            });
        }

        appointment.setStatus("CANCELLED");
        return appointmentRepository.save(appointment);
    }

    public List<AppointmentSlot> getSlotsForHospital(Long hospitalId) {
        return slotRepository.findByHospitalIdOrderBySlotTimeAsc(hospitalId);
    }

    public List<AppointmentSlot> getSlotsForDoctor(String doctorUsername) {
        return slotRepository.findByDoctorUsernameOrderBySlotTimeAsc(doctorUsername);
    }
}