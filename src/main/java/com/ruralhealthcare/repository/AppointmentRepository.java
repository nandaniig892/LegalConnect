package com.ruralhealthcare.repository;

import com.ruralhealthcare.entity.Appointment;
import com.ruralhealthcare.entity.AppointmentStatus;
import com.ruralhealthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findBySlot_Doctor(User doctor);
    List<Appointment> findByPatientAndStatus(User patient, AppointmentStatus status);
    boolean existsByPatientAndSlotId(User patient, Long slotId);
}