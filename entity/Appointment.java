package com.medvault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientUsername;

    private Long slotId;

    private Long hospitalId;
    private String hospitalName;

    private String doctorUsername;
    private String doctorName;

    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    private String reason;

    // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String status = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();

    public Appointment() {}

    public Appointment(String patientUsername, Long slotId, Long hospitalId, String hospitalName,
                        String doctorUsername, String doctorName,
                        LocalDateTime appointmentTime, String reason) {
        this.patientUsername = patientUsername;
        this.slotId = slotId;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.doctorUsername = doctorUsername;
        this.doctorName = doctorName;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public String getPatientUsername() { return patientUsername; }
    public void setPatientUsername(String patientUsername) { this.patientUsername = patientUsername; }
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getDoctorUsername() { return doctorUsername; }
    public void setDoctorUsername(String doctorUsername) { this.doctorUsername = doctorUsername; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}