package com.example.demo.entity;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "closed_days")
public class ClosedDay {
    @Id
    @Column(name = "close_days")
    private LocalDate closeDay;
    public ClosedDay() {
    }
    public ClosedDay(LocalDate closeDay) {
        this.closeDay = closeDay;
    }
    public LocalDate getCloseDay() {
        return closeDay;
    }
    public void setCloseDay(LocalDate closeDay) {
        this.closeDay = closeDay;
    }
}