package com.neu.alliance.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.neu.alliance.entity.Meeting;
import com.neu.alliance.entity.MeetingType;


import java.util.Date;

public class MeetingDTO {

    private int id;
    private String title;
    private String summary;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date start_time;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date end_time;

    private String location;
    private String organizer;
    private String imageUrl;
    private MeetingType type;

    public MeetingDTO(Meeting meeting) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.summary = meeting.getSummary();
        this.start_time = meeting.getStart_time();
        this.end_time = meeting.getEnd_time();
        this.location = meeting.getLocation();
        this.organizer = meeting.getOrganizer();
        this.imageUrl = meeting.getImageUrl();
        this.type = meeting.getType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MeetingType getType() {
        return type;
    }

    public void setType(MeetingType type) {
        this.type = type;
    }
}
