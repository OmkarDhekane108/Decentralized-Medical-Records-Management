package com.medvault.repository;

import com.medvault.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByHospitalIdOrderBySlotTimeAsc(Long hospitalId);
    List<AppointmentSlot> findByDoctorUsernameOrderBySlotTimeAsc(String doctorUsername);
}