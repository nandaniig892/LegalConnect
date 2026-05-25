package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.dto.BulkSyncRequest;
import com.ruralhealthcare.dto.HealthRecordSyncItem;
import com.ruralhealthcare.entity.HealthRecord;
import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.exception.ResourceNotFoundException;
import com.ruralhealthcare.repository.HealthRecordRepository;
import com.ruralhealthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final UserRepository userRepository;

    // Manual Constructor Injection
    public HealthRecordService(HealthRecordRepository healthRecordRepository, UserRepository userRepository) {
        this.healthRecordRepository = healthRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> syncRecords(BulkSyncRequest request, String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (HealthRecordSyncItem item : request.records()) {
            Optional<HealthRecord> existing = healthRecordRepository.findBySyncTokenHash(item.syncTokenHash());

            if (existing.isPresent()) {
                HealthRecord record = existing.get();
                // Last-Write-Wins: only update if incoming timestamp is newer
                if (item.recordTimestamp().isAfter(record.getRecordTimestamp())) {
                    record.setMetadataPayload(item.metadataPayload());
                    record.setRecordTimestamp(item.recordTimestamp());
                    record.setDiagnosisSummary(item.diagnosisSummary());
                    record.setSynced(true);
                    healthRecordRepository.save(record);
                    updated++;
                } else {
                    skipped++;
                }
            } else {
                // FIXED: Using standard object setters instead of Lombok Builder
                HealthRecord newRecord = new HealthRecord();
                newRecord.setPatient(patient);
                newRecord.setSyncTokenHash(item.syncTokenHash());
                newRecord.setMetadataPayload(item.metadataPayload());
                newRecord.setRecordTimestamp(item.recordTimestamp());
                newRecord.setDiagnosisSummary(item.diagnosisSummary());
                newRecord.setSynced(true);

                healthRecordRepository.save(newRecord);
                created++;
            }
        }

        return Map.of(
            "total", request.records().size(),
            "created", created,
            "updated", updated,
            "skipped", skipped
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPatientRecords(String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return healthRecordRepository.findByPatientOrderByRecordTimestampDesc(patient).stream()
                .map(r -> Map.<String, Object>of(
                    "id", r.getId(),
                    "syncTokenHash", r.getSyncTokenHash(),
                    "recordTimestamp", r.getRecordTimestamp().toString(),
                    "diagnosisSummary", r.getDiagnosisSummary() != null ? r.getDiagnosisSummary() : "",
                    "isSynced", r.isSynced()
                ))
                .toList();
    }
}