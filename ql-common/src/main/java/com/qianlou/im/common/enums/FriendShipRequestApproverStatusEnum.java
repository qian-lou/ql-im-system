package com.qianlou.im.common.enums;

public enum FriendShipRequestApproverStatusEnum {

    DEFAULT(0),
    /**
     * 1 同意；2 拒绝。
     */
    AGREE(1),

    REJECT(2),
    ;

    private final int code;

    FriendShipRequestApproverStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
