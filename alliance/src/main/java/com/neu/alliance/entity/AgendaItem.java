package com.neu.alliance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaItem implements Serializable {
    private Integer id;
    private Integer meetingId;
    private Date startTime;
    private String title;
    private String description;

}