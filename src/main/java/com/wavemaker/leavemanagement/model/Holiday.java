package com.wavemaker.leavemanagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "HOLIDAY")
public class Holiday {

    @Id
    @Column(name = "HOLIDAY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int holidayId;
    @Column(name = "HOLIDAY_NAME")
    String holidayName;
    @Column(name = "HOLIDAY_START_DATE")
    LocalDate holidayStartDate;
    @Column(name = "HOLIDAY_END_DATE")
    LocalDate holidayEndDate;
    @Column(name = "REASON")
    String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }


    public void setHolidayStartDate(LocalDate holidayStartDate) {
        this.holidayStartDate = holidayStartDate;
    }

    public LocalDate getHolidayEndDate() {
        return holidayEndDate;
    }

    public void setHolidayEndDate(LocalDate holidayEndDate) {
        this.holidayEndDate = holidayEndDate;
    }
}
