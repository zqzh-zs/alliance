package com.zhu.androidalliance.pojo.dataObject;

import com.zhu.androidalliance.enums.MeetingStatus;
import com.zhu.androidalliance.enums.MeetingType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meeting implements Serializable {
    private int id;
    private String title;
    private String summary;
    private Date startTime;
    private Date endTime;
    private String location;
    private String organizer;
    private String imageUrl;
    private MeetingType type;
    private String agenda;
    private List<AgendaItem> agendaItems;
    private List<Guest> guests;
    private MeetingStatus status;
    private String created_time;

}