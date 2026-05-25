package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.dto.SlotRequest;
import com.ruralhealthcare.entity.DoctorSlot;
import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.exception.ResourceNotFoundException;
import com.ruralhealthcare.exception.SlotConflictException;
import com.ruralhealthcare.repository.DoctorSlotRepository;
import com.ruralhealthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DoctorSlotService {

    private final DoctorSlotRepository slotRepository;
    private final UserRepository userRepository;

    // Manual Constructor for Dependency Injection (No Lombok needed)
    public DoctorSlotService(DoctorSlotRepository slotRepository, UserRepository userRepository) {
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> createSlot(SlotRequest request, String doctorEmail) {
        User doctor = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (request.startTime().isAfter(request.endTime()) || request.startTime().equals(request.endTime())) {
            throw new SlotConflictException("Start time must be before end time.");
        }

        boolean overlaps = slotRepository.existsOverlappingSlot(
                doctor, request.slotDate(), request.startTime(), request.endTime()
        );

        if (overlaps) {
            throw new SlotConflictException("A slot already exists overlapping this time window.");
        }

        // FIXED: Using standard object instantiation and setters instead of Lombok Builder
        DoctorSlot slot = new DoctorSlot();
        slot.setDoctor(doctor);
        slot.setSlotDate(request.slotDate());
        slot.setStartTime(request.startTime());
        slot.setEndTime(request.endTime());
        slot.setMaxPatients(request.maxPatients() != null ? request.maxPatients() : 1);
        slot.setBooked(false);

        DoctorSlot saved = slotRepository.save(slot);

        return Map.of(
            "id", saved.getId(),
            "slotDate", saved.getSlotDate().toString(),
            "startTime", saved.getStartTime().toString(),
            "endTime", saved.getEndTime().toString(),
            "maxPatients", saved.getMaxPatients()
        );
    }

    @Transactional
    public void deleteSlot(Long slotId, String doctorEmail) {
        DoctorSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        if (!slot.getDoctor().getEmail().equals(doctorEmail)) {
            throw new SecurityException("Not authorized to delete this slot");
        }

        if (slot.isBooked()) {
            throw new SlotConflictException("Cannot delete a booked slot. Cancel the appointment first.");
        }

        slotRepository.delete(slot);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableSlots() {
        return slotRepository.findBySlotDateGreaterThanEqualAndBookedFalse(LocalDate.now())
                .stream()
                .map(s -> Map.<String, Object>of(
                    "id", s.getId(),
                    "doctorId", s.getDoctor().getId(),
                    "doctorName", s.getDoctor().getFullName(),
                    "slotDate", s.getSlotDate().toString(),
                    "startTime", s.getStartTime().toString(),
                    "endTime", s.getEndTime().toString()
                ))
                .toList();
    }
}