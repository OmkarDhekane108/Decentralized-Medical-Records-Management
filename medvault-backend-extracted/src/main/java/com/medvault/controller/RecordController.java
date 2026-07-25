package com.medvault.controller;

import com.medvault.entity.AppUser;
import com.medvault.entity.MedicalRecord;
import com.medvault.repository.AppUserRepository;
import com.medvault.service.MedicalRecordService;
import com.medvault.service.TokenService;
import com.medvault.service.TokenService.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RecordController {

    private final MedicalRecordService recordService;
    private final AppUserRepository userRepository;
    private final TokenService tokenService;

    public RecordController(MedicalRecordService recordService, AppUserRepository userRepository, TokenService tokenService) {
        this.recordService = recordService;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    private Session requireSession(String authHeader) {
        Session s = tokenService.validate(authHeader);
        if (s == null) throw new SecurityException("Not authenticated");
        return s;
    }

    // --- Patient: view own records, upload a new one ---

    @GetMapping("/patient/records")
    public ResponseEntity<?> myRecords(@RequestHeader("Authorization") String auth) {
        Session s = requireSession(auth);
        return ResponseEntity.ok(recordService.forPatient(s.username()));
    }

    @PostMapping("/patient/records")
    public ResponseEntity<?> uploadRecord(@RequestHeader("Authorization") String auth, @RequestBody Map<String, String> body) {
        Session s = requireSession(auth);
        String doctorUsername = body.getOrDefault("doctorUsername", "doctor1");
        MedicalRecord record = recordService.createRecord(s.username(), doctorUsername, body.get("diagnosis"));
        return ResponseEntity.ok(record);
    }

    // --- Doctor: view panel records, add/update a diagnosis note ---

    @GetMapping("/doctor/records")
    public ResponseEntity<?> doctorRecords(@RequestHeader("Authorization") String auth) {
        Session s = requireSession(auth);
        return ResponseEntity.ok(recordService.forDoctor(s.username()));
    }

    @GetMapping("/doctor/patients")
    public ResponseEntity<?> patients(@RequestHeader("Authorization") String auth) {
        requireSession(auth);
        List<AppUser> patients = userRepository.findByRole("Patient");
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/doctor/records/{id}")
    public ResponseEntity<?> updateDiagnosis(@RequestHeader("Authorization") String auth,
                                              @PathVariable Long id,
                                              @RequestBody Map<String, String> body) {
        requireSession(auth);
        return ResponseEntity.ok(recordService.updateDiagnosis(id, body.get("diagnosis")));
    }

    // --- Admin: view everything, verify chain integrity ---

    @GetMapping("/admin/records")
    public ResponseEntity<?> allRecords(@RequestHeader("Authorization") String auth) {
        requireSession(auth);
        return ResponseEntity.ok(recordService.all());
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> allUsers(@RequestHeader("Authorization") String auth) {
        requireSession(auth);
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/admin/chain/verify")
    public ResponseEntity<?> verifyChain(@RequestHeader("Authorization") String auth) {
        requireSession(auth);
        return ResponseEntity.ok(Map.of("valid", recordService.verifyChainIntegrity()));
    }
}
