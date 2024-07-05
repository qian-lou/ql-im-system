package com.qianlou.im.common.enums;

import com.qianlou.im.common.execption.ApplicationExceptionEnum;
import lombok.Getter;

@Getter
public enum FriendShipRequestReadStatusEnum implements ApplicationExceptionEnum {
    READ(1, "已读"),
    UNREAD(0, "未读");


    private final int code;
    private final String message;
    FriendShipRequestReadStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getError() {
        return "";
    }
}
