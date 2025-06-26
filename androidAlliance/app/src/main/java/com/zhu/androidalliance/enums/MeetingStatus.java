package com.zhu.androidalliance.enums;

public enum MeetingStatus {

    DRAFT("草稿", "DRAFT"),
    PENDING("待审核", "PENDING"),
    APPROVED("已通过", "APPROVED"),
    REJECTED("已驳回", "REJECTED");

    private final String label;  // 中文含义或显示名
    private final String code;   // 数据库存的值

    MeetingStatus(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    // 根据 code 反向获取枚举
    public static MeetingStatus fromCode(String code) {
        for (MeetingStatus status : values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知会议状态: " + code);
    }
}
