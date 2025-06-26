package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "booth_detail")
public class BoothDetail {

    @Id
    @Column(name = "booth_number")
    private Integer boothNumber;

    @Column(name = "booth_place", nullable = false, length = 6)
    private String boothPlace;

    // --- Getter and Setter ---

    public Integer getBoothNumber() {
        return boothNumber;
    }

    public void setBoothNumber(Integer boothNumber) {
        this.boothNumber = boothNumber;
    }

    public String getBoothPlace() {
        return boothPlace;
    }

    public void setBoothPlace(String boothPlace) {
        this.boothPlace = boothPlace;
    }
}
