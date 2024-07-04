package com.qianlou.im.common.enums;

public enum FriendAllowTypeEnum {

    /**
     * 验证
     */
    NEED(2),

    /**
     * 不需要验证
     */
    NOT_NEED(1),

    ;


    private final int code;

    FriendAllowTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
