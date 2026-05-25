package com.ruralhealthcare.repository;

import com.ruralhealthcare.entity.MedicineInventory;
import com.ruralhealthcare.entity.StockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineInventoryRepository extends JpaRepository<MedicineInventory, Long> {
    List<MedicineInventory> findByPharmacyId(Long pharmacyId);
    List<MedicineInventory> findByPharmacyIdAndStockStatus(Long pharmacyId, StockStatus status);

    @Query("""
        SELECT m FROM MedicineInventory m
        WHERE m.pharmacy.id = :pharmacyId
          AND (LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')))
    """)
    List<MedicineInventory> searchByName(
        @Param("pharmacyId") Long pharmacyId,
        @Param("query") String query
    );

    @Query("""
        SELECT m FROM MedicineInventory m
        WHERE (LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')))
          AND m.stockStatus <> com.ruralhealthcare.entity.StockStatus.OUT_OF_STOCK
    """)
    List<MedicineInventory> searchAvailableGlobally(@Param("query") String query);
}
