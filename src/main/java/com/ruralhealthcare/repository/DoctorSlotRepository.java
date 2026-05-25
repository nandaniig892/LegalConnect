package com.ruralhealthcare.repository;

import com.ruralhealthcare.entity.DoctorSlot;
import com.ruralhealthcare.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, Long> {

    List<DoctorSlot> findByDoctorAndSlotDate(User doctor, LocalDate date);

    List<DoctorSlot> findBySlotDateGreaterThanEqualAndBookedFalse(LocalDate date);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<DoctorSlot> findWithLockById(Long id);

    @Query("""
        SELECT COUNT(s) > 0 FROM DoctorSlot s
        WHERE s.doctor = :doctor
          AND s.slotDate = :date
          AND s.startTime < :endTime
          AND s.endTime > :startTime
    """)
    boolean existsOverlappingSlot(
        @Param("doctor") User doctor,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
}
