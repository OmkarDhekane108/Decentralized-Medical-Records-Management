package com.medvault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_slots")
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Exactly one of hospitalId or doctorUsername should be set.
    private Long hospitalId;
    private String hospitalName;

    private String doctorUsername;
    private String doctorName;

    @Column(nullable = false)
    private LocalDateTime slotTime;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer booked = 0;

    public AppointmentSlot() {}

    public AppointmentSlot(Long hospitalId, String hospitalName, String doctorUsername, String doctorName,
                            LocalDateTime slotTime, Integer capacity) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.doctorUsername = doctorUsername;
        this.doctorName = doctorName;
        this.slotTime = slotTime;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getDoctorUsername() { return doctorUsername; }
    public void setDoctorUsername(String doctorUsername) { this.doctorUsername = doctorUsername; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalDateTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalDateTime slotTime) { this.slotTime = slotTime; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getBooked() { return booked; }
    public void setBooked(Integer booked) { this.booked = booked; }

    @Transient
    public Integer getRemaining() { return capacity - booked; }

    @Transient
    public String getStatus() { return getRemaining() > 0 ? "OPEN" : "CLOSED"; }
}