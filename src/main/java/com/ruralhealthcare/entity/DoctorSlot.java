package com.ruralhealthcare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_slots", indexes = {
    @Index(name = "idx_slot_doctor_date", columnList = "doctor_id, slot_date")
})
public class DoctorSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_booked", nullable = false)
    private boolean booked = false;

    @Column(name = "max_patients", nullable = false)
    private int maxPatients = 1;

    /**
     * Optimistic locking version field prevents concurrent double-booking.
     * Any concurrent update will fail with OptimisticLockException.
     */
    @Version
    private Long version;

    // 1. No-ArgsConstructor
    public DoctorSlot() {
    }

    // 2. All-ArgsConstructor
    public DoctorSlot(Long id, User doctor, LocalDate slotDate, LocalTime startTime, 
                      LocalTime endTime, boolean booked, int maxPatients, Long version) {
        this.id = id;
        this.doctor = doctor;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = booked;
        this.maxPatients = maxPatients;
        this.version = version;
    }

    // Standard Manual Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    // THE CRITICAL FIX: AppointmentService is looking for this exact method name
    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public int getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(int maxPatients) {
        this.maxPatients = maxPatients;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}