package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.dto.PrescriptionRequest;
import com.ruralhealthcare.entity.Appointment;
import com.ruralhealthcare.entity.Prescription;
import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.exception.ResourceNotFoundException;
import com.ruralhealthcare.repository.AppointmentRepository;
import com.ruralhealthcare.repository.PrescriptionRepository;
import com.ruralhealthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    // Manual Constructor for Dependency Injection (No Lombok)
    public PrescriptionService(PrescriptionRepository prescriptionRepository, 
                               AppointmentRepository appointmentRepository, 
                               UserRepository userRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> writePrescription(PrescriptionRequest request, String doctorEmail) {
        User doctor = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Validate that this appointment belongs to the logged-in doctor
        if (!appointment.getSlot().getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Not authorized to write prescription for this appointment");
        }

        LocalDateTime followUp = null;
        if (request.followUpDate() != null && !request.followUpDate().isBlank()) {
            try {
                // Support multiple formats: either full iso, or date-only
                if (request.followUpDate().contains("T")) {
                    followUp = LocalDateTime.parse(request.followUpDate());
                } else {
                    followUp = LocalDateTime.parse(request.followUpDate() + "T00:00:00");
                }
            } catch (Exception e) {
                // fall back to default follow-up of 1 week if parse fails
                followUp = LocalDateTime.now().plusWeeks(1);
            }
        }

        // Check if a prescription already exists for this appointment
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointment.getId())
                .orElse(null);

        if (prescription == null) {
            // FIXED: Using standard setters instead of Lombok Builder
            prescription = new Prescription();
            prescription.setAppointment(appointment);
            prescription.setDoctor(doctor);
            prescription.setMedicineList(request.medicineList());
            prescription.setInstructions(request.instructions());
            prescription.setDiagnosis(request.diagnosis());
            prescription.setFollowUpDate(followUp);
            prescription.setDispensed(false);
        } else {
            prescription.setMedicineList(request.medicineList());
            prescription.setInstructions(request.instructions());
            prescription.setDiagnosis(request.diagnosis());
            prescription.setFollowUpDate(followUp);
        }

        Prescription saved = prescriptionRepository.save(prescription);

        return Map.of(
            "id", saved.getId(),
            "appointmentId", appointment.getId(),
            "doctorName", doctor.getFullName(),
            "medicineList", saved.getMedicineList(),
            "instructions", saved.getInstructions() != null ? saved.getInstructions() : "",
            "diagnosis", saved.getDiagnosis() != null ? saved.getDiagnosis() : "",
            "followUpDate", saved.getFollowUpDate() != null ? saved.getFollowUpDate().toString() : ""
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPrescriptionByAppointment(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for appointment " + appointmentId));

        return Map.of(
            "id", prescription.getId(),
            "appointmentId", appointmentId,
            "doctorName", prescription.getDoctor().getFullName(),
            "medicineList", prescription.getMedicineList(),
            "instructions", prescription.getInstructions() != null ? prescription.getInstructions() : "",
            "diagnosis", prescription.getDiagnosis() != null ? prescription.getDiagnosis() : "",
            "followUpDate", prescription.getFollowUpDate() != null ? prescription.getFollowUpDate().toString() : "",
            "isDispensed", prescription.isDispensed(),
            "issuedAt", prescription.getIssuedAt() != null ? prescription.getIssuedAt().toString() : ""
        );
    }
}