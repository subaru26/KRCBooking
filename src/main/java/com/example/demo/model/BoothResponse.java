package com.example.demo.model;
import java.time.LocalTime;
public class BoothResponse {
    private LocalTime startTime;
    private LocalTime endTime;
    private int boothNumber;
    public BoothResponse(LocalTime start, LocalTime end, int boothNumber) {
        this.startTime = start;
        this.endTime = end;
        this.boothNumber = boothNumber;
    }
    
    public BoothResponse() {
        
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    public int getBoothNumber() {
        return boothNumber;
    }
    public void setBoothNumber(int boothNumber) {
        this.boothNumber = boothNumber;
    }
}