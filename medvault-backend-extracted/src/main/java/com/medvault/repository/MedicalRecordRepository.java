package com.medvault.repository;

import com.medvault.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientUsernameOrderByDateDesc(String patientUsername);
    List<MedicalRecord> findByDoctorUsernameOrderByDateDesc(String doctorUsername);
}
