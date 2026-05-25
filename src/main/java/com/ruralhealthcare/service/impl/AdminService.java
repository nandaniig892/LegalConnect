package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.entity.Role;
import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.exception.ResourceNotFoundException;
import com.ruralhealthcare.repository.AppointmentRepository;
import com.ruralhealthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    // Manual Constructor for Dependency Injection (Replaces @RequiredArgsConstructor)
    public AdminService(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalDoctors = userRepository.findAllByRole(Role.ROLE_DOCTOR).size();
        long totalPatients = userRepository.findAllByRole(Role.ROLE_PATIENT).size();
        long totalPharmacies = userRepository.findAllByRole(Role.ROLE_PHARMACY).size();
        long pendingApprovals = userRepository.findAllByRoleAndIsApproved(Role.ROLE_DOCTOR, false).size();
        long totalAppointments = appointmentRepository.count();

        return Map.of(
            "totalUsers", totalUsers,
            "totalDoctors", totalDoctors,
            "totalPatients", totalPatients,
            "totalPharmacies", totalPharmacies,
            "pendingDoctorApprovals", pendingApprovals,
            "totalAppointments", totalAppointments
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingDoctors() {
        return userRepository.findAllByRoleAndIsApproved(Role.ROLE_DOCTOR, false).stream()
                .map(u -> Map.<String, Object>of(
                    "id", u.getId(),
                    "email", u.getEmail(),
                    "fullName", u.getFullName(),
                    "phoneNumber", u.getPhoneNumber() != null ? u.getPhoneNumber() : ""
                ))
                .toList();
    }

    @Transactional
    public Map<String, Object> approveDoctor(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (doctor.getRole() != Role.ROLE_DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }

        doctor.setApproved(true);
        userRepository.save(doctor);

        return Map.of("message", "Doctor approved successfully", "doctorId", doctorId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> Map.<String, Object>of(
                    "id", u.getId(),
                    "email", u.getEmail(),
                    "fullName", u.getFullName(),
                    "role", u.getRole().name(),
                    "isApproved", u.isApproved(),
                    "isEnabled", u.isEnabled()
                ))
                .toList();
    }
}