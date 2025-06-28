package com.neu.alliance.entity;

import java.io.Serializable;
import java.util.Date;

public class AgendaItem implements Serializable {
    private Date startTime;
    private String title;
    private String description;

    public AgendaItem() {
    }
    public AgendaItem(Date startTime, String title, String description) {
        this.startTime = startTime;
        this.title = title;
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}