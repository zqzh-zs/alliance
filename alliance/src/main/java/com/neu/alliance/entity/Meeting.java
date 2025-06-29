package com.neu.alliance.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meeting implements Serializable {
    private int id;
    private String title;
    private String summary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date start_time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date end_time;


    private String location;
    private String organizer;

    private String imageUrl;
    private MeetingType type;
    private List<AgendaItem> agendaItems;
    private List<Guest> guests;

    private MeetingStatus status;
    private String create_time;

}