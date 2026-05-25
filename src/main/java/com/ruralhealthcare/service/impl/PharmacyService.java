package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.dto.InventoryRequest;
import com.ruralhealthcare.entity.MedicineInventory;
import com.ruralhealthcare.entity.Pharmacy;
import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.exception.ResourceNotFoundException;
import com.ruralhealthcare.repository.MedicineInventoryRepository;
import com.ruralhealthcare.repository.PharmacyRepository;
import com.ruralhealthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final MedicineInventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    // Manual Constructor for Spring Dependency Injection
    public PharmacyService(PharmacyRepository pharmacyRepository, 
                           MedicineInventoryRepository inventoryRepository, 
                           UserRepository userRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchNearby(Double lat, Double lng, Double radiusKm, String medicineName) {
        List<Pharmacy> pharmacies = pharmacyRepository.findWithinRadius(lat, lng, radiusKm);

        return pharmacies.stream().map(pharmacy -> {
            List<MedicineInventory> items = medicineName != null && !medicineName.isBlank()
                    ? inventoryRepository.searchByName(pharmacy.getId(), medicineName)
                    : inventoryRepository.findByPharmacyId(pharmacy.getId());

            Map<String, Object> result = Map.of(
                "pharmacyId", pharmacy.getId(),
                "name", pharmacy.getName(),
                "address", pharmacy.getAddress() != null ? pharmacy.getAddress() : "",
                "contactNumber", pharmacy.getContactNumber() != null ? pharmacy.getContactNumber() : "",
                "inventory", items.stream().map(i -> Map.<String, Object>of(
                    "id", i.getId(),
                    "medicineName", i.getMedicineName(),
                    "genericName", i.getGenericName() != null ? i.getGenericName() : "",
                    "stockStatus", i.getStockStatus().name(),
                    "quantity", i.getQuantity(),
                    "unitPrice", i.getUnitPrice() != null ? i.getUnitPrice() : 0.0
                )).collect(Collectors.toList())
            );
            return result;
        }).toList();
    }

    @Transactional
    public Map<String, Object> upsertInventory(InventoryRequest request, String pharmacyUserEmail) {
        User user = userRepository.findByEmail(pharmacyUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pharmacy pharmacy = pharmacyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy profile not found for this account"));

        // FIXED: Using standard setters instead of Lombok Builder
        MedicineInventory item = new MedicineInventory();
        item.setPharmacy(pharmacy);
        item.setMedicineName(request.medicineName());
        item.setGenericName(request.genericName());
        item.setManufacturer(request.manufacturer());
        item.setQuantity(request.quantity());
        item.setUnitPrice(request.unitPrice());

        MedicineInventory saved = inventoryRepository.save(item);

        return Map.of(
            "id", saved.getId(),
            "medicineName", saved.getMedicineName(),
            "stockStatus", saved.getStockStatus().name(),
            "quantity", saved.getQuantity()
        );
    }

    @Transactional
    public Map<String, Object> updateInventory(Long itemId, InventoryRequest request, String pharmacyUserEmail) {
        MedicineInventory item = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));

        User user = userRepository.findByEmail(pharmacyUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pharmacy pharmacy = pharmacyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy profile not found"));

        if (!item.getPharmacy().getId().equals(pharmacy.getId())) {
            throw new SecurityException("Not authorized to update this inventory item");
        }

        item.setMedicineName(request.medicineName());
        item.setGenericName(request.genericName());
        item.setManufacturer(request.manufacturer());
        item.setQuantity(request.quantity());
        item.setUnitPrice(request.unitPrice());

        MedicineInventory saved = inventoryRepository.save(item);

        return Map.of(
            "id", saved.getId(),
            "medicineName", saved.getMedicineName(),
            "stockStatus", saved.getStockStatus().name(),
            "quantity", saved.getQuantity()
        );
    }
}