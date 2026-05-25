package com.ruralhealthcare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records", indexes = {
    @Index(name = "idx_health_record_patient", columnList = "patient_id"),
    @Index(name = "idx_health_record_sync_hash", columnList = "sync_token_hash", unique = true)
})
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Column(name = "sync_token_hash", nullable = false, unique = true)
    private String syncTokenHash;

    @Column(name = "metadata_payload", columnDefinition = "TEXT", nullable = false)
    private String metadataPayload;

    @Column(name = "is_synced", nullable = false)
    private boolean synced = false;

    @Column(name = "record_timestamp", nullable = false)
    private LocalDateTime recordTimestamp;

    @Column(name = "diagnosis_summary")
    private String diagnosisSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 1. No-ArgsConstructor
    public HealthRecord() {
    }

    // 2. All-ArgsConstructor
    public HealthRecord(Long id, User patient, String syncTokenHash, String metadataPayload, 
                        boolean synced, LocalDateTime recordTimestamp, String diagnosisSummary, LocalDateTime createdAt) {
        this.id = id;
        this.patient = patient;
        this.syncTokenHash = syncTokenHash;
        this.metadataPayload = metadataPayload;
        this.synced = synced;
        this.recordTimestamp = recordTimestamp;
        this.diagnosisSummary = diagnosisSummary;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Standard Manual Getters and Setters (The Ultimate Fix for Eclipse)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public String getSyncTokenHash() {
        return syncTokenHash;
    }

    public void setSyncTokenHash(String syncTokenHash) {
        this.syncTokenHash = syncTokenHash;
    }

    public String getMetadataPayload() {
        return metadataPayload;
    }

    public void setMetadataPayload(String metadataPayload) {
        this.metadataPayload = metadataPayload;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public LocalDateTime getRecordTimestamp() {
        return recordTimestamp;
    }

    public void setRecordTimestamp(LocalDateTime recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }

    public String getDiagnosisSummary() {
        return diagnosisSummary;
    }

    public void setDiagnosisSummary(String diagnosisSummary) {
        this.diagnosisSummary = diagnosisSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}