package com.qianlou.im.common.enums;

import lombok.Getter;

@Getter
public enum FriendShipStatusEnum {

    /**
     * 0未添加 1正常 2删除
     */
    FRIEND_STATUS_NO_FRIEND(0),

    FRIEND_STATUS_NORMAL(1),

    FRIEND_STATUS_DELETE(2),

    /**
     * 0未添加 1正常 2删除
     */
    BLACK_STATUS_NORMAL(1),
    BLACK_STATUS_BLACKED(2),
    ;

    private final int code;

    FriendShipStatusEnum(int code){
        this.code=code;
    }

}
