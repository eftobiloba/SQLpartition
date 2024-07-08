package com.example.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class User {

    @Id
    private String userId;
    private String name;
    private String address;
    private String email;
    private LocalDateTime dob;
    private String phone;
    @Column(nullable = false)
    private LocalDate date;

    public User() {
    }

    public User(String userId, String name, String address, String email, LocalDateTime dob, String phone, LocalDate date) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.email = email;
        this.dob = dob;
        this.phone = phone;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDob() {
        return dob;
    }
    public LocalDate getDate() {
        return date;
    }

    public String getPhone() {
        return phone;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(LocalDateTime dob) {
        this.dob = dob;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
