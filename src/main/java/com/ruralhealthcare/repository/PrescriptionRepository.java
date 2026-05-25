package com.ruralhealthcare.repository;

import com.ruralhealthcare.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointmentId(Long appointmentId);
    List<Prescription> findByDoctorId(Long doctorId);
    List<Prescription> findByAppointment_Patient_Id(Long patientId);
}
