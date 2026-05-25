package com.ruralhealthcare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicine_inventory", indexes = {
    @Index(name = "idx_inventory_pharmacy", columnList = "pharmacy_id"),
    @Index(name = "idx_inventory_medicine_name", columnList = "medicine_name")
})
public class MedicineInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "generic_name")
    private String genericName;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "quantity", nullable = false)
    private int quantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status", nullable = false)
    private StockStatus stockStatus = StockStatus.OUT_OF_STOCK;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 1. No-ArgsConstructor
    public MedicineInventory() {
    }

    // 2. All-ArgsConstructor
    public MedicineInventory(Long id, Pharmacy pharmacy, String medicineName, String genericName, 
                             String manufacturer, int quantity, StockStatus stockStatus, Double unitPrice, LocalDateTime updatedAt) {
        this.id = id;
        this.pharmacy = pharmacy;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.stockStatus = stockStatus;
        this.unitPrice = unitPrice;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (quantity <= 0) {
            stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (quantity <= 10) {
            stockStatus = StockStatus.LOW_STOCK;
        } else {
            stockStatus = StockStatus.IN_STOCK;
        }
    }

    // Standard Manual Getters and Setters (The Ultimate Fix for Eclipse)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    // This will fix the 'getGenericName() is undefined' error in PharmacyService
    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public StockStatus getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(StockStatus stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}