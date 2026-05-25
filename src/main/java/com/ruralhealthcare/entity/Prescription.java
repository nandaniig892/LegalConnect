package com.ruralhealthcare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions", indexes = {
    @Index(name = "idx_prescription_appointment", columnList = "appointment_id")
})
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "medicine_list", columnDefinition = "TEXT", nullable = false)
    private String medicineList;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "is_dispensed", nullable = false)
    private boolean dispensed = false;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    // 1. No-ArgsConstructor
    public Prescription() {
    }

    // 2. All-ArgsConstructor
    public Prescription(Long id, Appointment appointment, User doctor, String medicineList, 
                        String instructions, String diagnosis, LocalDateTime followUpDate, 
                        boolean dispensed, LocalDateTime issuedAt) {
        this.id = id;
        this.appointment = appointment;
        this.doctor = doctor;
        this.medicineList = medicineList;
        this.instructions = instructions;
        this.diagnosis = diagnosis;
        this.followUpDate = followUpDate;
        this.dispensed = dispensed;
        this.issuedAt = issuedAt;
    }

    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
    }

    // Standard Manual Getters and Setters (The Ultimate Fix for Eclipse)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public String getMedicineList() {
        return medicineList;
    }

    public void setMedicineList(String medicineList) {
        this.medicineList = medicineList;
    }

    // This exact manual method will instantly fix the PrescriptionService error
    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }

    public boolean isDispensed() {
        return dispensed;
    }

    public void setDispensed(boolean dispensed) {
        this.dispensed = dispensed;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
}