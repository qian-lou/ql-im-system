package com.qianlou.im.service.friendship.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("t_friendship_group_member")
public class FriendShipGroupMemberEntity {

    @TableId(value = "group_id")
    private Long groupId;

    private String toId;

}
