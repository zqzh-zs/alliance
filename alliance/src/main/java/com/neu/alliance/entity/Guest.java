package com.neu.alliance.entity;

import java.io.Serializable;

public class Guest implements Serializable {
    private String name;
    private String title;
    private String organization;
    private String avatarUrl;

    public Guest()
    {
    }

    public Guest(String name, String title, String organization, String avatarUrl) {
        this.name = name;
        this.title = title;
        this.organization = organization;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}