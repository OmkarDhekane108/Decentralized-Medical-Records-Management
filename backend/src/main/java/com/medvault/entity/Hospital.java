package com.medvault.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double lat;
    private Double lng;

    private String specialization;

    private Integer slots = 0;

    private String address;
    private String phone;

    public Hospital() {}

    public Hospital(String name, Double lat, Double lng, String specialization,
                     Integer slots, String address, String phone) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.specialization = specialization;
        this.slots = slots;
        this.address = address;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public Integer getSlots() { return slots; }
    public void setSlots(Integer slots) { this.slots = slots; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}