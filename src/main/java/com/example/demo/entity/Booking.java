package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    @Column(name = "facilities_type", nullable = false, length = 10)
    private String facilitiesType;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "canceled", nullable = false)
    private Boolean canceled = false;

    // --- Getter and Setter ---

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFacilitiesType() {
        return facilitiesType;
    }

    public void setFacilitiesType(String facilitiesType) {
        this.facilitiesType = facilitiesType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }
}
