package com.ruralhealthcare.repository;

import com.ruralhealthcare.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    Optional<Pharmacy> findByUserId(Long userId);

    @Query("""
        SELECT p FROM Pharmacy p
        WHERE p.active = true
          AND (6371 * acos(
                cos(radians(:lat)) * cos(radians(p.latitude)) *
                cos(radians(p.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(p.latitude))
              )) <= :radiusKm
        ORDER BY (6371 * acos(
                cos(radians(:lat)) * cos(radians(p.latitude)) *
                cos(radians(p.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(p.latitude))
              )) ASC
    """)
    List<Pharmacy> findWithinRadius(
        @Param("lat") Double latitude,
        @Param("lng") Double longitude,
        @Param("radiusKm") Double radiusKm
    );
}
