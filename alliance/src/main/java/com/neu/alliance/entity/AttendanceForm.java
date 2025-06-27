package com.neu.alliance.entity;

import java.io.Serializable;

public class AttendanceForm implements Serializable {
    private String name;
    private String organization;
    private String phone;
    private String email;
    private String trip;
    private String time;

    public AttendanceForm() {
    }

    public AttendanceForm(String name, String organization, String phone, String email, String trip, String time) {
        this.name = name;
        this.organization = organization;
        this.phone = phone;
        this.email = email;
        this.trip = trip;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
