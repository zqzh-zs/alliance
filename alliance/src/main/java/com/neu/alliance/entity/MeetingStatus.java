package com.neu.alliance.entity;

public enum MeetingStatus {

    PENDING("待审核", "PENDING"),
    APPROVED("已通过", "APPROVED"),
    REJECTED("已驳回", "REJECTED"),

    OVER("已结束", "OVER");
    private final String displayName; // 中文显示
    private final String code;        // 数据库存储

    MeetingStatus(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    // 根据code从数据库读的值转换成枚举
    public static MeetingStatus fromCode(String code) {
        for (MeetingStatus status : MeetingStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with code " + code);
    }
}
