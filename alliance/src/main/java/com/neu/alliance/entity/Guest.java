package com.neu.alliance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest implements Serializable {
    private Integer id;
    private Integer meetingId;
    private String name;
    private String title;
    private String organization;


}