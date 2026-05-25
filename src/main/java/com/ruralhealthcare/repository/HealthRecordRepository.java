package com.ruralhealthcare.repository;

import com.ruralhealthcare.entity.HealthRecord;
import com.ruralhealthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByPatientOrderByRecordTimestampDesc(User patient);
    Optional<HealthRecord> findBySyncTokenHash(String syncTokenHash);
    boolean existsBySyncTokenHash(String syncTokenHash);
}
