package com.neu.alliance.entity;

public enum MeetingType {
    SEMINAR("会议研讨"),
    STANDARD("标准定制"),
    TRAINING("技术培训"),
    TOOL_DEV("工具研发"),
    PUBLIC_WELFARE("公益行动");

    private final String displayName;

    MeetingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}