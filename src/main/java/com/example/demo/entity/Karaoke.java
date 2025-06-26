package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "karaoke")
public class Karaoke {

    @Id
    @Column(name = "booking_id")
    private Integer bookingId;

    @OneToOne
    @MapsId  // Bookingの主キーをKaraokeの主キーとして利用
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "user_number", nullable = false)
    private Integer userNumber;

    @Column(name = "is_check", nullable = false)
    private Boolean isCheck;

    // --- Getter and Setter ---

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public Boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Boolean isCheck) {
        this.isCheck = isCheck;
    }
}
