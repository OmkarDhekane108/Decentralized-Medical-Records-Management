package com.medvault.service;

import com.medvault.entity.Hospital;
import com.medvault.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public List<Hospital> getAll() {
        return hospitalRepository.findAll();
    }

    public List<Hospital> getBySpecialization(String specialization) {
        return hospitalRepository.findBySpecializationIgnoreCase(specialization);
    }

    public Hospital add(Hospital hospital) {
        return hospitalRepository.save(hospital);
    }

    public void delete(Long id) {
        hospitalRepository.deleteById(id);
    }
}