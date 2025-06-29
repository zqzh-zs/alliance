package com.neu.alliance.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class AttendanceFormDTO implements Serializable {
    private String name;
    private String organization;
    private String phone;
    private String email;
    private String trip;
    private String time;

}
