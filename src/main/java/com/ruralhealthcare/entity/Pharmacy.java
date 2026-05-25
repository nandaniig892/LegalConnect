package com.ruralhealthcare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pharmacies", indexes = {
    @Index(name = "idx_pharmacy_location", columnList = "latitude, longitude")
})
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // 1. No-ArgsConstructor
    public Pharmacy() {
    }

    // 2. All-ArgsConstructor
    public Pharmacy(Long id, User user, String name, Double latitude, Double longitude, 
                    String contactNumber, String address, boolean active) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactNumber = contactNumber;
        this.address = address;
        this.active = active;
    }

    // Standard Manual Getters and Setters (The Fix for Eclipse)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // This will clear getContactNumber() undefined error
    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // This will clear getAddress() undefined error in PharmacyService
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}