package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.dto.AppointmentRequest;
import com.ruralhealthcare.entity.*;
import com.ruralhealthcare.exception.ResourceNotFoundException;
import com.ruralhealthcare.exception.SlotConflictException;
import com.ruralhealthcare.repository.AppointmentRepository;
import com.ruralhealthcare.repository.DoctorSlotRepository;
import com.ruralhealthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorSlotRepository slotRepository;
    private final UserRepository userRepository;

    // Manual Constructor for Dependency Injection
    public AppointmentService(AppointmentRepository appointmentRepository, 
                              DoctorSlotRepository slotRepository, 
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> bookAppointment(AppointmentRequest request, String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (appointmentRepository.existsByPatientAndSlotId(patient, request.slotId())) {
            throw new SlotConflictException("You already have an appointment for this slot.");
        }

        DoctorSlot slot = slotRepository.findWithLockById(request.slotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        if (slot.isBooked()) {
            throw new SlotConflictException("This slot is no longer available.");
        }

        slot.setBooked(true);
        slotRepository.save(slot);

        // Fixed: Using standard Java Constructor instead of Lombok Builder
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setSlot(slot);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPatientNotes(request.patientNotes());

        Appointment saved = appointmentRepository.save(appointment);

        return Map.of(
            "appointmentId", saved.getId(),
            "status", saved.getStatus().name(),
            "slotDate", slot.getSlotDate().toString(),
            "startTime", slot.getStartTime().toString(),
            "doctorId", slot.getDoctor().getId(),
            "doctorName", slot.getDoctor().getFullName()
        );
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, String userEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        boolean isPatient = appointment.getPatient().getEmail().equals(userEmail);
        boolean isDoctor = appointment.getSlot().getDoctor().getEmail().equals(userEmail);

        if (!isPatient && !isDoctor) {
            throw new SecurityException("Not authorized to cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.getSlot().setBooked(false);
        appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPatientAppointments(String email) {
        User patient = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return appointmentRepository.findByPatient(patient).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDoctorAppointments(String email) {
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return appointmentRepository.findBySlot_Doctor(doctor).stream()
                .map(this::toSummary)
                .toList();
    }

    private Map<String, Object> toSummary(Appointment a) {
        return Map.of(
            "id", a.getId(),
            "status", a.getStatus().name(),
            "patientName", a.getPatient().getFullName(),
            "doctorName", a.getSlot().getDoctor().getFullName(),
            "slotDate", a.getSlot().getSlotDate().toString(),
            "startTime", a.getSlot().getStartTime().toString(),
            "endTime", a.getSlot().getEndTime().toString()
        );
    }
}