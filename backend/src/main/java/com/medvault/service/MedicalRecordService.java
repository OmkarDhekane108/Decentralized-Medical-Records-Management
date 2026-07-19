package com.medvault.service;

import com.medvault.blockchain.BlockchainService;
import com.medvault.entity.MedicalRecord;
import com.medvault.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final BlockchainService blockchainService;
    private final IPFSService ipfsService;

    public MedicalRecordService(MedicalRecordRepository recordRepository,
                                 BlockchainService blockchainService,
                                 IPFSService ipfsService) {
        this.recordRepository = recordRepository;
        this.blockchainService = blockchainService;
        this.ipfsService = ipfsService;
    }

    public MedicalRecord createRecord(String patientUsername, String doctorUsername, String diagnosis) {
        MedicalRecord record = new MedicalRecord();
        record.setPatientUsername(patientUsername);
        record.setDoctorUsername(doctorUsername);
        record.setDiagnosis(diagnosis);
        record.setDate(LocalDate.now());
        record.setStatus("Open");

        // 1) Pin the record payload to IPFS (best-effort — null if no node configured)
        String payload = String.format(
                "{\"patient\":\"%s\",\"doctor\":\"%s\",\"diagnosis\":\"%s\",\"date\":\"%s\"}",
                patientUsername, doctorUsername, diagnosis, record.getDate());
        record.setIpfsCid(ipfsService.addContent(payload));

        // 2) Anchor the same payload on the blockchain for tamper-evidence
        record.setBlockHash(blockchainService.addBlock(payload));

        return recordRepository.save(record);
    }

    public MedicalRecord updateDiagnosis(Long recordId, String diagnosis) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordId));
        record.setDiagnosis(diagnosis);
        // Re-anchor the amended content as a new block — the old block still proves the prior version existed.
        record.setBlockHash(blockchainService.addBlock(diagnosis + "|" + LocalDate.now()));
        return recordRepository.save(record);
    }

    public List<MedicalRecord> forPatient(String patientUsername) {
        return recordRepository.findByPatientUsernameOrderByDateDesc(patientUsername);
    }

    public List<MedicalRecord> forDoctor(String doctorUsername) {
        return recordRepository.findByDoctorUsernameOrderByDateDesc(doctorUsername);
    }

    public List<MedicalRecord> all() {
        return recordRepository.findAll();
    }

    public boolean verifyChainIntegrity() {
        return blockchainService.isChainValid();
    }
}
