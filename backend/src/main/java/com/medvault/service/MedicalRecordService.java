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

    public MedicalRecord createRecord(String patientUsername, String doctorUsername, String diagnosis,
                                      String prescription, String attachmentData, String attachmentName, String attachmentType, String status) {
        MedicalRecord record = new MedicalRecord();
        record.setPatientUsername(patientUsername);
        record.setDoctorUsername(doctorUsername);
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setAttachmentData(attachmentData);
        record.setAttachmentName(attachmentName);
        record.setAttachmentType(attachmentType);
        record.setDate(LocalDate.now());
        record.setStatus(status != null ? status : "Open");

        // 1) Pin the record payload to IPFS (best-effort)
        String payload = String.format(
                "{\"patient\":\"%s\",\"doctor\":\"%s\",\"diagnosis\":\"%s\",\"prescription\":\"%s\",\"date\":\"%s\"}",
                patientUsername, doctorUsername, diagnosis, prescription != null ? prescription : "", record.getDate());
        record.setIpfsCid(ipfsService.addContent(payload));

        // 2) Anchor payload on the blockchain for tamper-evidence
        record.setBlockHash(blockchainService.addBlock(payload));

        return recordRepository.save(record);
    }

    public MedicalRecord createRecord(String patientUsername, String doctorUsername, String diagnosis,
                                      String prescription, String attachmentData, String attachmentName, String attachmentType) {
        return createRecord(patientUsername, doctorUsername, diagnosis, prescription, attachmentData, attachmentName, attachmentType, "Open");
    }

    public MedicalRecord updateRecord(Long recordId, String diagnosis, String prescription, String attachmentData, String attachmentName, String attachmentType, String status) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordId));
        if (diagnosis != null) record.setDiagnosis(diagnosis);
        if (prescription != null) record.setPrescription(prescription);
        if (attachmentData != null) {
            record.setAttachmentData(attachmentData);
            record.setAttachmentName(attachmentName);
            record.setAttachmentType(attachmentType);
        }
        if (status != null) record.setStatus(status);
        // Re-anchor amended content on blockchain
        record.setBlockHash(blockchainService.addBlock(diagnosis + "|" + (prescription != null ? prescription : "") + "|" + LocalDate.now()));
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
