package com.neu.alliance.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum ResultStatusEnum {
    SUCCESS(200),
    FAILED(-1);

    @Getter
    int code;
}
